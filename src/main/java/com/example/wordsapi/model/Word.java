package com.example.wordsapi.model;

import jakarta.persistence.*;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Word must not be blank")
    @Size(max = 255, message = "Word must not exceed 255 characters")
    @Column(name = "word")
    private String name;

    @NotBlank(message = "Definition must not be blank")
    @Column(name = "definition")
    private String definition;

    @NotBlank(message = "Example must not be blank")
    @Column(name = "example")
    private String example;

    public Word() {
    }
}
