package com.example.demo.domain.association;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.word.WordModelReturn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class AssociationModelAdd extends GeneralResultModel {
    @Schema(name = "Word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID word;
    @Schema(name = "Translation id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID translation;
}
