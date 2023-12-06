package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.association.AssociationModelAdd;
import com.example.demo.domain.association.AssociationModelReturn;
import com.example.demo.domain.association.AssociationModelReturnEnriched;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.infrastructure.repositories.association.AssociationEntity;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import com.example.demo.infrastructure.repositories.word.WordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring"
)
public interface AssociationMapper {
    AssociationModelReturn toAssociationModelReturn(AssociationEntity association);

    AssociationEntity toAssociationEntity(AssociationModelReturn association);

    AssociationEntity toAssociationEntity(AssociationModelAdd model);

    List<AssociationEntity> toAssociationEntityList(List<AssociationModelAdd> modelAddList);

    List<AssociationModelReturn> toAssociationModelList(List<AssociationEntity> list);


    @Mapping(source = "associationEntity.id", target = "id")
    @Mapping(source = "associationEntity.word", target = "word")
    @Mapping(source = "associationEntity.translation", target = "translation")
    @Mapping(source = "wordEntity.word", target = "firstWordText")
    @Mapping(source = "translationEntity.word", target = "secondWordText")
    @Mapping(source = "wordEntity.languageId", target = "firstWordLangId")
    @Mapping(source = "translationEntity.languageId", target = "secondWordLangId")
    @Mapping(source = "languageEntity.name", target = "firstWordLangName")
    @Mapping(source = "translationLanguageEntity.name", target = "secondWordLangName")
    AssociationModelReturnEnriched toAssociationModelReturnEnriched(AssociationEntity associationEntity, WordEntity wordEntity, WordEntity translationEntity, LanguageEntity languageEntity, LanguageEntity translationLanguageEntity);
}
