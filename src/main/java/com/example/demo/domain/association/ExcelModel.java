package com.example.demo.domain.association;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class ExcelModel {
    String word;
    String wordLanguage;
    String translate;
    String translateLanguage;
    LocalDateTime createdAt;
    String createdByFullName;
    String createdByLogin;
    String createdByRole;
}
