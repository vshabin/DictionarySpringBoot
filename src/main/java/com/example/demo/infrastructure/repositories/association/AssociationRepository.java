package com.example.demo.infrastructure.repositories.association;

import com.example.demo.infrastructure.repositories.AssociationMapper;

import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.BeanRepository;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class AssociationRepository {
    @Inject
    AssociationMapper mapStructMapper;
}
