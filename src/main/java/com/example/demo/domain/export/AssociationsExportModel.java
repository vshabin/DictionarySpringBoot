package com.example.demo.domain.export;

import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class AssociationsExportModel {
    WordModelReturnEnriched word;
    WordModelReturnEnriched translation;
    LocalDateTime createdAt;
    UserModelReturn user;
}
