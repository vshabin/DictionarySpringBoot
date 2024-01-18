package com.example.demo.infrastructure.repositories.word;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.domain.word.WordCriteriaModel;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.domainservices.LanguageService;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapperInterfaces.WordMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class WordRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";
    @Inject
    private DbServer dbServer;
    @Inject
    private LanguageService languageService;
    @Inject
    private WordMapper mapStructMapper;

    public WordModelReturn getByName(String word) {
        WordEntity wordEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.WORD, word).findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }

    public WordModelReturn getById(UUID id) {
        WordEntity wordEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, id).findOne();
        return mapStructMapper.toWordModelReturn(wordEntity);
    }

    @Transactional
    public GuidResultModel save(WordModelPost model) {
        WordEntity entity = mapStructMapper.toWordEntity(model);
        try {
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getId());
    }

    @Transactional
    public WordModelReturn update(WordModelReturn model) {
        WordEntity entity = mapStructMapper.toWordEntity(model);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            return new WordModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return model;
    }

    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try {
            dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, id).delete();
        } catch (Exception e) {
            resultModel = new GeneralResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        return null;
    }

    @Transactional
    public List<WordModelReturn> saveList(List<WordModelPost> modelAddList) {
        List<WordModelReturn> resultModel = new ArrayList<>();
        List<WordEntity> entities = mapStructMapper.toWordEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new WordModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage()));
            return resultModel;
        }
        for (WordEntity entity : entities) {
            resultModel.add(mapStructMapper.toWordModelReturn(entity));
        }
        return resultModel;
    }

    public List<String> findExist(List<String> name) {
        return mapStructMapper.toWordModelReturnList(dbServer.getDB().find(WordEntity.class).where().in(WordEntity.WORD, name).findList()).stream().map(WordModelReturn::getWord).collect(Collectors.toList());
    }

    public PageResult<WordModelReturn> criteriaQuery(WordCriteriaModel criteriaModel) {
        PagedList<WordEntity> entityPagedList = createExpression(criteriaModel, dbServer.getDB().find(WordEntity.class).setFirstRow(criteriaModel.getSize() * (criteriaModel.getPageNumber() - 1)).setMaxRows(criteriaModel.getSize()).where()).findPagedList();
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

    public WordModelReturnEnriched getByNameEnriched(String word) {
        WordEntity wordEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.WORD, word).findOne();
        LanguageModelReturn languageModel = null;
        if (wordEntity != null) {
            languageModel = languageService.getById(wordEntity.getLanguageId());
        }
        return mapStructMapper.toWordModelReturnEnriched(wordEntity, languageModel);
    }

    public WordModelReturnEnriched getByIdEnriched(UUID id) {
        var wordEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, id).findOne();
        LanguageModelReturn languageModel = null;
        if (wordEntity != null) {
            languageModel = languageService.getById(wordEntity.getLanguageId());
        }
        return mapStructMapper.toWordModelReturnEnriched(wordEntity, languageModel);
    }

    public boolean isSameLanguage(UUID firstWord, UUID secondWord) {
        UUID firstLang = dbServer.getDB().find(WordEntity.class).select(WordEntity.LANGUAGE).where().eq(WordEntity.ID, firstWord).findSingleAttribute();
        UUID secondLang = dbServer.getDB().find(WordEntity.class).select(WordEntity.LANGUAGE).where().eq(WordEntity.ID, secondWord).findSingleAttribute();
        return firstLang == secondLang;
    }

    public boolean exists(UUID id) {
        return dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, id).exists();
    }

    public boolean exists(String word) {
        return dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.WORD, word).exists();
    }

    public List<WordModelReturn> getListEnrichedByIdList(Collection<UUID> ids) {
        var words = dbServer.getDB()
                .find(WordEntity.class)
                .where()
                .in(WordEntity.ID, ids)
                .findList();
        return mapStructMapper.toWordModelReturnList(words);
    }
}
