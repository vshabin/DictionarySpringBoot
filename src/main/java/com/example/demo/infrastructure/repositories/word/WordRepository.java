package com.example.demo.infrastructure.repositories.word;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.WordMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class WordRepository {
    @Inject
    private DbServer dbServer;
    @Inject
    private WordMapper mapStructMapper;

    public WordModelReturn getByName(String word) {
        WordEntity wordEntity = dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq(WordEntity.WORD, word)
                .findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }

    public WordModelReturn getById(UUID id) {
        WordEntity wordEntity = dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq(WordEntity.ID, id)
                .findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }

    @Transactional
    public GeneralResultModel save(WordModelPost model) {
        GeneralResultModel resultModel;
        WordEntity entity = mapStructMapper.toWordEntity(model);
        try {
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            resultModel = new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage());
            return resultModel;
        }
        resultModel = new GuidResultModel(entity.getId());
        return resultModel;
    }

    @Transactional
    public GeneralResultModel update(WordModelReturn model) {
        GeneralResultModel resultModel;
        WordEntity entity = mapStructMapper.toWordEntity(model);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            resultModel = new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage());
            return resultModel;
        }
        resultModel = model;
        return resultModel;
    }

    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try {
            dbServer.getDB().find(WordEntity.class)
                    .where()
                    .eq(WordEntity.ID, id)
                    .delete();
        } catch (Exception e) {
            resultModel = new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage());
            return resultModel;
        }
        resultModel = new GeneralResultModel();
        return resultModel;
    }

    @Transactional
    public List<GeneralResultModel> saveList(List<WordModelPost> modelAddList) {
        List<GeneralResultModel> resultModel = new ArrayList<>();
        List<WordEntity> entities = mapStructMapper.toWordEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage()));
            return resultModel;
        }
        for (WordEntity entity : entities) {
            resultModel.add(mapStructMapper.toWordModelReturn(entity));
        }
        return resultModel;
    }

    public List<String> findExist(List<String> name) {
        return mapStructMapper.toWordModelReturnList(dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .in(WordEntity.WORD, name)
                .findList()).stream().map(WordModelReturn::getWord).collect(Collectors.toList());
    }

    public PageResult<WordModelReturn> criteriaQuery(WordCriteriaModel criteriaModel) {
        PagedList<WordEntity> entityPagedList = createExpression(criteriaModel,
                dbServer.getDB()
                        .find(WordEntity.class)
                        .setFirstRow(criteriaModel.getSize() * (criteriaModel.getPageNumber() - 1))
                        .setMaxRows(criteriaModel.getSize())
                        .where())
                .findPagedList();
        return new PageResult<>(mapStructMapper.toWordModelList(entityPagedList.getList()), entityPagedList.getTotalCount());
    }

    private ExpressionList<WordEntity> createExpression(WordCriteriaModel criteriaModel, ExpressionList<WordEntity> expr) {
        if (StringUtils.isNotBlank(criteriaModel.getWordFilter())) {
            expr.like(WordEntity.WORD, escape(criteriaModel.getWordFilter(), '%'));
        }
        if (criteriaModel.getLangIdFilter() != null) {
            expr.eq(WordEntity.LANGUAGE, criteriaModel.getLangIdFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getLangNameFilter())) {
            var languages = dbServer.getDB().find(LanguageEntity.class).where().like(LanguageEntity.NAME, escape(criteriaModel.getLangNameFilter(), '%')).findIds();
            expr.in(WordEntity.LANGUAGE, languages);
        }
        if (criteriaModel.getFromFilter() != null) {
            expr.ge(WordEntity.CREATED_AT, criteriaModel.getFromFilter());
        }
        if (criteriaModel.getToFilter() != null) {
            expr.le(WordEntity.CREATED_AT, criteriaModel.getToFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getSortFilter())) {
            expr.orderBy(criteriaModel.getSortFilter());
        }
        return expr;
    }

    private String escape(String string, char esc) {
        return esc + string + esc;
    }

    public GeneralResultModel getByNameEnriched(String word) {
        WordEntity wordEntity = dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq(WordEntity.WORD, word)
                .findOne();
        LanguageEntity languageEntity;
        if (wordEntity != null) {
            languageEntity = dbServer.getDB()
                    .find(LanguageEntity.class)
                    .where()
                    .eq(LanguageEntity.ID, wordEntity.getLanguageId())
                    .findOne();
        } else {
            return new GeneralResultModel("INCORRECT_NAME_ERROR", "Нет слова с таким именем");
        }
        return mapStructMapper.toWordModelReturnEnriched(wordEntity, languageEntity);
    }

    public GeneralResultModel getByIdEnriched(UUID id) {
        WordEntity wordEntity = dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .eq(WordEntity.ID, id)
                .findOne();
        LanguageEntity languageEntity;
        if (wordEntity != null) {
            languageEntity = dbServer.getDB()
                    .find(LanguageEntity.class)
                    .where()
                    .eq(LanguageEntity.ID, wordEntity.getLanguageId())
                    .findOne();
        } else {
            return new GeneralResultModel("INCORRECT_ID_ERROR", "Нет слова с таким id");
        }
        return mapStructMapper.toWordModelReturnEnriched(wordEntity, languageEntity);
    }

    public boolean isSameLanguage(UUID firstWord, UUID secondWord) {
        UUID firstLang = dbServer.getDB()
                .find(WordEntity.class)
                .select(WordEntity.LANGUAGE)
                .where()
                .eq(WordEntity.ID, firstWord)
                .findSingleAttribute();
        UUID secondLang = dbServer.getDB()
                .find(WordEntity.class)
                .select(WordEntity.LANGUAGE)
                .where()
                .eq(WordEntity.ID, secondWord)
                .findSingleAttribute();
        return firstLang == secondLang;
    }
}
