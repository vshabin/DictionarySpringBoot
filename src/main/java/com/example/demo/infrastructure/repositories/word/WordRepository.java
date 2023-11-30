package com.example.demo.infrastructure.repositories.word;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.word.WordModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.WordMapper;
import io.ebean.Transaction;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Repository
public class WordRepository{
    @Inject
    private DbServer dbServer;
    @Inject
    private WordMapper mapStructMapper;

    public List<WordModelReturn> findAll(){
        List<WordEntity> entityList=dbServer.getDB()
                .find(WordEntity.class)
                .findList();
        List<WordModelReturn> modelList= mapStructMapper.toWordModelReturnList(entityList);
        return modelList;
    }

    public WordModelReturn getWordByName(String word){
        WordEntity wordEntity=dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq("word", word)
                .findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }
    public WordModelReturn getWordById(UUID id){
        WordEntity wordEntity=dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq("id", id)
                .findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }
    public List<WordModel> getAllWordByDictionaryId(UUID id){
        List<WordEntity> entityList=dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq("language",id)
                .findList();
        List<WordModel> modelList= mapStructMapper.toWordModelList(entityList);
        return modelList;
    }
    @Transactional
    public GeneralResultModel save(WordModelPost model){
        GeneralResultModel resultModel;
        WordEntity entity= mapStructMapper.toWordEntity(model);
        try(var tr= Transaction.current()){

            dbServer.getDB().save(entity);
            tr.commit();
        }
        catch (Exception e){
            resultModel=new GeneralResultModel("DATABASE_TRANSACTION_ERROR","Ошибка проведения транзакции: "+ e.getMessage());
            return resultModel;
        }
        resultModel=new GuidResultModel(entity.getId());
        return resultModel;
    }

}
