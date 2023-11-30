package com.example.demo.domain.language;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.Data;

import java.util.UUID;

@Data
public class LanguageModelReturn extends GeneralResultModel {
    private UUID id;
    private String name;
}
