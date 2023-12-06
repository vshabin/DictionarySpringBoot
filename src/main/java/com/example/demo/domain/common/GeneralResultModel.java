package com.example.demo.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResultModel {
    @Schema(name = "Error code", example = "INCORRECT_ID_ERROR", required = false, hidden = true)
    private String errorCode;
    @Schema(name = "Error message", example = "Нет слова с таким id", required = false, hidden = true)
    private String errorMessage;
}
