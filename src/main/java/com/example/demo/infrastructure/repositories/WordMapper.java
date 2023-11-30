package com.example.demo.infrastructure.repositories;


import com.example.demo.domain.word.WordModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.word.WordEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = LanguageMapper.class
)
public interface WordMapper {
    List<WordModel> toWordModelList (List<WordEntity> words);
    WordModel toWordModel(WordEntity word);
    WordEntity toWordEntity(WordModel model);
    WordEntity toWordEntity(WordModelPost model);
    WordModelReturn toWordModelReturn(WordEntity entity);
    List<WordModelReturn> toWordModelReturnList(List<WordEntity> entity);
}
