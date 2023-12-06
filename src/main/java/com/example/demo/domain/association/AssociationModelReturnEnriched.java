package com.example.demo.domain.association;

import lombok.Data;

import java.util.UUID;

@Data
public class AssociationModelReturnEnriched extends AssociationModelReturn {
    private String firstWordText;
    private String secondWordText;

    private UUID firstWordLangId;
    private UUID secondWordLangId;

    private String firstWordLangName;
    private String secondWordLangName;
}
