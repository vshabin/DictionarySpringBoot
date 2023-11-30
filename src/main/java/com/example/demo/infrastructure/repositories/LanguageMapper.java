package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface LanguageMapper {
    LanguageModelReturn toLanguageModelReturn(LanguageEntity languageEntity);

    LanguageEntity toLanguageEntity(LanguageModelAdd languageModel);

    LanguageEntity toLanguageEntity(LanguageModelReturn languageModel);

    List<LanguageModelReturn> toListLanguageModelReturn(List<LanguageEntity> languageEntities);

}
