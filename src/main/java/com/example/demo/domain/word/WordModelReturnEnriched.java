package com.example.demo.domain.word;

import lombok.Data;

@Data
public class WordModelReturnEnriched extends WordModelReturn {
    //enrich
    private String languageName;
}
