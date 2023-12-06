package com.example.demo.domain.language;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.word.WordModelReturn;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LanguageContent extends GeneralResultModel {
    private UUID id;
    private String name;
    private List<WordModelReturn> words;
}
