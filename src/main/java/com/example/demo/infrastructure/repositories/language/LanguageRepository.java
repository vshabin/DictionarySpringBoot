package com.example.demo.infrastructure.repositories.language;

import com.example.demo.domain.LanguageModel;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapStructMapper;
import io.ebean.BeanRepository;
import io.ebean.Transaction;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Repository
public class LanguageRepository{
    @Inject
    DbServer dbServer;
    @Inject
    MapStructMapper mapStructMapper;
    public List<LanguageModel> findAll(){
        return mapStructMapper.listLanguageEntityToListLanguageModel(dbServer.getDB()
                .find(LanguageEntity.class)
                .findList());
    }
    public LanguageModel findByName(String name){
        return mapStructMapper.languageEntityToLanguageModel(dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .eq("name", name)
                .findOne());
    }
    @Transactional
    public UUID save(LanguageModel model){
        LanguageEntity entity;
        try(var tr=Transaction.current()){
            entity= mapStructMapper.languageModelToLanguageEntity(model);
            dbServer.getDB().save(entity);

            tr.commit();
        }
        return entity.getUuid();
    }
}
