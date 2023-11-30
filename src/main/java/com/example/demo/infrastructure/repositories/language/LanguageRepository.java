package com.example.demo.infrastructure.repositories.language;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.LanguageMapper;
import io.ebean.Lists;
import io.ebean.Transaction;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class LanguageRepository {
    @Inject
    DbServer dbServer;
    @Inject
    LanguageMapper mapStructMapper;

    public List<LanguageModelReturn> findAll() {
        return mapStructMapper.toListLanguageModelReturn(dbServer.getDB()
                .find(LanguageEntity.class)
                .findList());
    }

    public LanguageModelReturn findByName(String name) {
        return mapStructMapper.toLanguageModelReturn(dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .eq("name", name)
                .findOne());
    }

    public LanguageModelReturn findById(UUID id) {
        LanguageEntity languageEntity = dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .eq("id", id)
                .findOne();
        return mapStructMapper.toLanguageModelReturn(languageEntity);
    }

    @Transactional
    public GeneralResultModel save(LanguageModelAdd model) {
        GeneralResultModel resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(model);
        try{
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            resultModel=new GeneralResultModel("DATABASE_TRANSACTION_ERROR","Ошибка проведения транзакции: "+ e.getMessage());
            return resultModel;
        }
        resultModel = new GuidResultModel(entity.getId());
        return resultModel;
    }

    @Transactional
    public GeneralResultModel update(LanguageModelReturn languageModel) {
        GeneralResultModel resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(languageModel);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            resultModel=new GeneralResultModel("DATABASE_TRANSACTION_ERROR","Ошибка проведения транзакции: "+ e.getMessage());
            return resultModel;
        }
        resultModel = languageModel;
        return resultModel;
    }
    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try{
            dbServer.getDB().find(LanguageEntity.class)
                    .where()
                    .eq("id", id)
                    .delete();
        } catch (Exception e) {
            resultModel=new GeneralResultModel("DATABASE_TRANSACTION_ERROR","Ошибка проведения транзакции: "+ e.getMessage());
            return resultModel;
        }
        resultModel= new GeneralResultModel();
        return resultModel;
    }
    @Transactional
    public List<GeneralResultModel> saveList(List<LanguageModelAdd> modelAddList) {
        List<GeneralResultModel> resultModel=new ArrayList<>();
        List<LanguageEntity> entities = mapStructMapper.toLanguageEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new GeneralResultModel("DATABASE_TRANSACTION_ERROR","Ошибка проведения транзакции: "+ e.getMessage()));
            return resultModel;
        }
        for (LanguageEntity entity: entities){
            resultModel.add(new GuidResultModel(entity.getId()));
        }
        return resultModel;
    }
}
