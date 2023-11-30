package com.example.demo.domain.word;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.Data;

import java.util.UUID;

@Data
public class WordModel extends GeneralResultModel {
    private UUID id;
    private String word;
    private UUID languageId;
}
