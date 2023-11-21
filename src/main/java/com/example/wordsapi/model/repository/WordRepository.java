package com.example.wordsapi.model.repository;
import com.example.wordsapi.model.Word;
import com.example.wordsapi.model.WordDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Integer> {

    @Query("SELECT new com.example.wordsapi.model.WordDTO(w.id, w.name) FROM Word w")
    List<WordDTO> findAllWordsWithIdAndName();

    @Query("SELECT w FROM Word w WHERE w.name LIKE %:fragment% OR w.definition LIKE %:fragment% OR w.example LIKE %:fragment%")
    List<Word> findByNameContainingOrDefinitionContainingOrExampleContaining(@Param("fragment") String fragment);
}