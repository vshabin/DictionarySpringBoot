package com.example.demo.infrastructure.repositories.word;

import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapStructMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.BeanRepository;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class WordRepository{
    @Inject
    MapStructMapper mapStructMapper;

}
