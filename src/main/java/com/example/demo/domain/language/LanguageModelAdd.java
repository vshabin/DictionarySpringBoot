package com.example.demo.domain.language;

import com.example.demo.domain.common.GeneralResultModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LanguageModelAdd extends GeneralResultModel {
    @Schema(name = "Language name", example = "Japan", required = true)
    @NotBlank(message = "Name should be not empty")
    private String name;
}
