package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.association.AssociationModel;
import com.example.demo.infrastructure.repositories.association.AssociationEntity;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface AssociationMapper {
    AssociationModel associationEntityToAssociationModel(AssociationEntity association);
    AssociationEntity associationModelToAssociationEntity(AssociationModel association);
}
