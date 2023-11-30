package com.example.demo.domain.association;

import com.example.demo.domain.word.WordModel;
import lombok.Data;

import java.util.UUID;

@Data
public class AssociationModel {
    UUID uuid;
    WordModel firstWord;
    WordModel secondWord;
}
