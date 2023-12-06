package com.example.demo.domain.association;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class AssociationModelReturn extends AssociationModelAdd {
    @Schema(name = "Association id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = true)
    private UUID id;
}
