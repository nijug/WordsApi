package com.example.wordsapi.controller;

import com.example.wordsapi.model.Word;
import com.example.wordsapi.model.WordDTO;
import com.example.wordsapi.model.repository.WordRepository;
import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Validator;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;



@RestController
@RequestMapping("/words")
public class WordController {

    private final WordRepository wordRepository;
    private final Validator validator;
    @Autowired
    public WordController(WordRepository wordRepository, Validator validator) {
        this.wordRepository = wordRepository;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<List<WordDTO>> getAllWords() {
        List<WordDTO> wordDTOs = wordRepository.findAllWordsWithIdAndName();
        return ResponseEntity.ok(wordDTOs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Word> getWordById(@PathVariable String id) {
        try {
            Integer numericId = Integer.parseInt(id);

            if (numericId <= 0) {
                return ResponseEntity.badRequest().body(null);
            }

            Optional<Word> optionalWord = wordRepository.findById(numericId);
            return optionalWord.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search/{fragment}")
    public ResponseEntity<List<Word>> searchWords(@PathVariable String fragment) {
        List<Word> matchingWords = wordRepository.findByNameContainingOrDefinitionContainingOrExampleContaining(fragment);
        if (matchingWords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(matchingWords);
        }
    }
    @PostMapping
    public ResponseEntity<?> addWord(@RequestBody Word word, @RequestHeader("Authorization") String authorizationHeader) {
        if (!isValidAuthorization(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        try {

            Set<ConstraintViolation<Word>> violations = validator.validate(word);
            if (!violations.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            Word savedWord = wordRepository.save(word);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWord(@PathVariable String id, @RequestBody(required = false) Word word, @RequestHeader("Authorization") String authorizationHeader) {
        if (!isValidAuthorization(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Integer numericId = Integer.parseInt(id);
            if (numericId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (word == null) {
                return ResponseEntity.status(HttpStatus.OK).body(null);
            }

            if (!wordRepository.existsById(numericId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Set<ConstraintViolation<Word>> violations = validator.validate(word);
            if (!violations.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            word.setId(numericId);
            Word updatedWord = wordRepository.save(word);
            return ResponseEntity.ok(updatedWord);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        if (!isValidAuthorization(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        try {
            Integer numericId = Integer.parseInt(id);

            if (numericId <= 0) {
                return ResponseEntity.badRequest().body(null);
            }

            if (!wordRepository.existsById(numericId)) {
                return ResponseEntity.notFound().build();
            }

            wordRepository.deleteById(numericId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private boolean isValidAuthorization(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            try {
                String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));

                String[] parts = credentials.split(":", 2);
                String username = parts[0].trim();
                String password = parts[1].trim();


                return "password".equals(password);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}