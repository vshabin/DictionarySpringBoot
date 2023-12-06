package com.example.demo.domain.language;

import com.example.demo.domain.common.GeneralResultModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class LanguageModelReturn extends LanguageModelAdd {
    @Schema(name = "Language UUID", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID id;
    @Schema(name = "Date of creation", example = "2023-12-03 18:59:00.655995 +00:00", required = false, hidden = true)
    private LocalDateTime createdAt;
}
