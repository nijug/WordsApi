package com.example.wordsapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordDTO {

    private Integer id;
    private String name;


    public WordDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public WordDTO() {
    }
}