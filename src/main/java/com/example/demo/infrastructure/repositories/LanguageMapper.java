package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.language.LanguageModelPost;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface LanguageMapper {
    LanguageModelReturn toLanguageModelReturn(LanguageEntity language);

    LanguageEntity toLanguageEntity(LanguageModelPost language);

    List<LanguageModelReturn> toListLanguageModelReturn(List<LanguageEntity> languages);

}
