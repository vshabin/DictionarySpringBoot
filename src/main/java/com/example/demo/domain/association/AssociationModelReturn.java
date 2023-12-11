package com.example.demo.domain.association;

import com.example.demo.domain.common.GeneralResultModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssociationModelReturn extends GeneralResultModel {
    @Schema(name = "Association id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID id;
    @Schema(name = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID word;
    @Schema(name = "Translation id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID translation;


    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss.SSS")
    @Schema(name = "Date of creation", example = "2023-12-03 18:59:00.655995 +00:00", required = false, hidden = true)
    private LocalDateTime createdAt;

    public AssociationModelReturn(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
