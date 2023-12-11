package com.example.demo.domain.language;

import com.example.demo.domain.common.GeneralResultModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.ebeaninternal.server.util.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageModelReturn extends GeneralResultModel {
    @Schema(name = "Language name", example = "Japan", required = true)
    @NotBlank(message = "Name should be not empty")
    private String name;
    @Schema(name = "Language UUID", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID id;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss.SSS")
    @Schema(name = "Date of creation", example = "2023-12-03 18:59:00.655995 +00:00", required = false, hidden = true)
    private LocalDateTime createdAt;

    public LanguageModelReturn(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
