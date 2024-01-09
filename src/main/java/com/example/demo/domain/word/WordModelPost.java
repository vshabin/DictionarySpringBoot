package com.example.demo.domain.word;

import com.example.demo.domain.common.GeneralResultModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class WordModelPost extends GeneralResultModel {
    @Schema(name = "Word", example = "Собака", required = true)
    @NotBlank(message = "Name should be not empty")
    private String word;
    @Schema(name = "Language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID languageId;
}
