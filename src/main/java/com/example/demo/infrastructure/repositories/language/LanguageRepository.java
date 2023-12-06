package com.example.demo.infrastructure.repositories.language;

import com.example.demo.domain.common.*;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.LanguageMapper;
import io.ebean.*;
import io.ebean.annotation.Transactional;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class LanguageRepository {
    @Inject
    DbServer dbServer;
    @Inject
    LanguageMapper mapStructMapper;

    public LanguageModelReturn findByName(String name) {
        LanguageEntity entity = dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .eq(LanguageEntity.NAME, name)
                .findOne();
        return mapStructMapper.toLanguageModelReturn(entity);
    }

    public LanguageModelReturn findById(UUID id) {
        LanguageEntity entity = dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .eq(LanguageEntity.ID, id)
                .findOne();
        return mapStructMapper.toLanguageModelReturn(entity);
    }

    @Transactional
    public GeneralResultModel save(LanguageModelAdd model) {
        GeneralResultModel resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(model);
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
    public GeneralResultModel update(LanguageModelReturn languageModel) {
        GeneralResultModel resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(languageModel);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            resultModel = new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage());
            return resultModel;
        }
        resultModel = languageModel;
        return resultModel;
    }

    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try {
            dbServer.getDB().find(LanguageEntity.class)
                    .where()
                    .eq(LanguageEntity.ID, id)
                    .delete();
        } catch (Exception e) {
            resultModel = new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage());
            return resultModel;
        }
        resultModel = new GeneralResultModel();
        return resultModel;
    }

    @Transactional
    public List<GeneralResultModel> saveList(List<LanguageModelAdd> modelAddList) {
        List<GeneralResultModel> resultModel = new ArrayList<>();
        List<LanguageEntity> entities = mapStructMapper.toLanguageEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new GeneralResultModel("DATABASE_TRANSACTION_ERROR", "Ошибка проведения транзакции: " + e.getMessage()));
            return resultModel;
        }
        for (LanguageEntity entity : entities) {
            resultModel.add(mapStructMapper.toLanguageModelReturn(entity));
        }
        return resultModel;
    }

    public List<String> findExist(List<String> name) {
        return mapStructMapper.toLanguageModelReturnList(dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .in(LanguageEntity.NAME, name)
                .findList()).stream().map(LanguageModelReturn::getName).collect(Collectors.toList());
    }

    public PageResult<LanguageModelReturn> criteriaQuery(LanguageCriteriaModel languageCriteriaModel) {
        PagedList<LanguageEntity> entityPagedList = createExpression(languageCriteriaModel,
                dbServer.getDB()
                        .find(LanguageEntity.class)
                        .setFirstRow(languageCriteriaModel.getSize() * (languageCriteriaModel.getPageNumber() - 1))
                        .setMaxRows(languageCriteriaModel.getSize())
                        .where())
                .findPagedList();
        return new PageResult<>(mapStructMapper.toLanguageModelReturnList(entityPagedList.getList()), entityPagedList.getTotalCount());
    }

    private ExpressionList<LanguageEntity> createExpression(LanguageCriteriaModel languageCriteriaModel, ExpressionList<LanguageEntity> expr) {
        if (StringUtils.isNotBlank(languageCriteriaModel.getNameFilter())) {
            expr.like(LanguageEntity.NAME, escape(languageCriteriaModel.getNameFilter(), '%'));
        }
        if (languageCriteriaModel.getFromFilter() != null) {
            expr.ge(LanguageEntity.CREATED_AT, languageCriteriaModel.getFromFilter());
        }
        if (languageCriteriaModel.getToFilter() != null) {
            expr.le(LanguageEntity.CREATED_AT, languageCriteriaModel.getToFilter());
        }
        if (StringUtils.isNotBlank(languageCriteriaModel.getSortFilter())) {
            expr.orderBy(languageCriteriaModel.getSortFilter());
        }
        return expr;
    }

    private String escape(String string, char esc) {
        return esc + string + esc;
    }
}
