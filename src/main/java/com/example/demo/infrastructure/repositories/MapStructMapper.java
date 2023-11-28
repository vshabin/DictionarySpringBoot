package com.example.demo.infrastructure.repositories;


import com.example.demo.domain.AssociationModel;
import com.example.demo.domain.LanguageModel;
import com.example.demo.domain.WordModel;
import com.example.demo.infrastructure.repositories.association.AssociationEntity;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import com.example.demo.infrastructure.repositories.word.WordEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface MapStructMapper {
    WordModel wordEntityToWordModel(WordEntity word);
    AssociationModel associationEntityToAssociationModel(AssociationEntity association) ;
    LanguageModel languageEntityToLanguageModel(LanguageEntity language);
//    WordEntity wordModelToWordEntity(WordModel word);
    AssociationEntity associationModelToAssociationEntity(AssociationModel association) ;
    LanguageEntity languageModelToLanguageEntity(LanguageModel language);
    List<LanguageModel> listLanguageEntityToListLanguageModel(List<LanguageEntity> language);
    WordEntity toEntity(WordModel model);
}
