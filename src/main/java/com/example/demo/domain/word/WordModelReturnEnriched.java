package com.example.demo.domain.word;

import com.example.demo.domain.common.GeneralResultModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class WordModelReturnEnriched extends GeneralResultModel {
    @Schema(name = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID id;
    @Schema(name = "Word", example = "Собака", required = true)
    @NotBlank(message = "Name should be not empty")
    private String word;
    @Schema(name = "Language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID languageId;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss.SSS")
    @Schema(name = "Date of creation", example = "2023-12-03 18:59:00.655995 +00:00", required = false, hidden = true)
    private LocalDateTime createdAt;

    //enrich
    private String languageName;

    public WordModelReturnEnriched(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
