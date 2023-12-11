package com.example.demo.infrastructure.repositories;


import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import com.example.demo.infrastructure.repositories.word.WordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = LanguageMapper.class
)
public interface WordMapper {
    List<WordModelReturn> toWordModelList(List<WordEntity> words);

    WordEntity toWordEntity(WordModelReturn model);

    WordEntity toWordEntity(WordModelPost model);

    WordModelReturn toWordModelReturn(WordEntity entity);

    List<WordModelReturn> toWordModelReturnList(List<WordEntity> entity);

    List<WordEntity> toWordEntityList(List<WordModelPost> modelAddList);

    @Mapping(source = "languageEntity.name", target = "languageName")
    @Mapping(source = "wordEntity.id", target = "id")
    @Mapping(source = "wordEntity.createdAt", target = "createdAt")
    WordModelReturnEnriched toWordModelReturnEnriched(WordEntity wordEntity, LanguageEntity languageEntity);
}
