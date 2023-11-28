package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AssociationModel {
    UUID uuid;
    WordModel firstWord;
    WordModel secondWord;
}
