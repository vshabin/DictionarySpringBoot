package com.example.demo.infrastructure.repositories.language;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.language.LanguageCriteriaModel;
import com.example.demo.domain.language.LanguageModelAdd;
import com.example.demo.domain.language.LanguageModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.LanguageMapper;
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
public class LanguageRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";

    @Inject
    DbServer dbServer;
    @Inject
    LanguageMapper mapStructMapper;

    public LanguageModelReturn findByName(String name) {
        LanguageEntity entity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.NAME, name).findOne();
        return mapStructMapper.toLanguageModelReturn(entity);
    }

    public LanguageModelReturn findById(UUID id) {
        LanguageEntity entity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.ID, id).findOne();
        return mapStructMapper.toLanguageModelReturn(entity);
    }

    @Transactional
    public GuidResultModel save(LanguageModelAdd model) {
        GuidResultModel resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(model);
        try {
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            resultModel = new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        resultModel = new GuidResultModel(entity.getId());
        return resultModel;
    }

    @Transactional
    public LanguageModelReturn update(LanguageModelReturn languageModel) {
        LanguageModelReturn resultModel;
        LanguageEntity entity = mapStructMapper.toLanguageEntity(languageModel);
        try {
            dbServer.getDB().update(LanguageEntity.class).set(LanguageEntity.NAME, entity.getName()).where().eq(LanguageEntity.ID, entity.getId()).update();
        } catch (Exception e) {
            resultModel = new LanguageModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        resultModel = languageModel;
        return resultModel;
    }

    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try {
            dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.ID, id).delete();
        } catch (Exception e) {
            resultModel = new GeneralResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        return null;
    }

    @Transactional
    public List<LanguageModelReturn> saveList(List<LanguageModelAdd> modelAddList) {
        List<LanguageModelReturn> resultModel = new ArrayList<>();
        List<LanguageEntity> entities = mapStructMapper.toLanguageEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new LanguageModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage()));
            return resultModel;
        }
        for (LanguageEntity entity : entities) {
            resultModel.add(mapStructMapper.toLanguageModelReturn(entity));
        }
        return resultModel;
    }

    public List<String> findExist(List<String> name) {
        return mapStructMapper.toLanguageModelReturnList(dbServer.getDB().find(LanguageEntity.class).where().in(LanguageEntity.NAME, name).findList()).stream().map(LanguageModelReturn::getName).collect(Collectors.toList());
    }

    public PageResult<LanguageModelReturn> criteriaQuery(LanguageCriteriaModel languageCriteriaModel) {
        PagedList<LanguageEntity> entityPagedList = createExpression(languageCriteriaModel, dbServer.getDB().find(LanguageEntity.class).setFirstRow(languageCriteriaModel.getSize() * (languageCriteriaModel.getPageNumber() - 1)).setMaxRows(languageCriteriaModel.getSize()).where()).findPagedList();
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

    public List<UUID> findExistById(List<UUID> languageIds) {
        return mapStructMapper.toLanguageModelReturnList(dbServer.getDB().find(LanguageEntity.class).where().in(LanguageEntity.ID, languageIds).findList()).stream().map(LanguageModelReturn::getId).collect(Collectors.toList());
    }

    public boolean exists(UUID id) {
        return dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.ID, id).exists();
    }

    public boolean exists(String name) {
        return dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.NAME, name).exists();
    }

    public List<LanguageModelReturn> getListByIdList(List<UUID> ids) {
        return mapStructMapper.toLanguageModelReturnList(dbServer.getDB()
                .find(LanguageEntity.class)
                .where()
                .in(LanguageEntity.ID,ids)
                .findList());
    }
}
