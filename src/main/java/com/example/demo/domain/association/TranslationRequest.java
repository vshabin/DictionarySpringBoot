package com.example.demo.domain.association;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TranslationRequest extends PageModel {
    @Schema(name = "word", description = "word to translate", example = "Собака", required = true)
    @NotBlank
    private String word;
    @Schema(name = "word language", description = "language of word", example = "Russian", required = true)
    @NotBlank
    private String wordLanguage;
    @Schema(name = "translation language", description = "language to translate", example = "English", required = true)
    @NotBlank
    private String translationLanguage;
}
