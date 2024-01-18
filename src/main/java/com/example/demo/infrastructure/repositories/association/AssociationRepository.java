package com.example.demo.infrastructure.repositories.association;

import com.example.demo.domain.association.*;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.word.WordModelReturn;
import com.example.demo.infrastructure.repositories.MapperInterfaces.AssociationMapper;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapperInterfaces.WordMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import com.example.demo.infrastructure.repositories.word.WordEntity;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AssociationRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";

    @Inject
    private AssociationMapper mapStructMapper;
    @Inject
    private WordMapper wordMapper;
    @Inject
    private DbServer dbServer;

    public AssociationModelReturn findById(UUID id) {
        AssociationEntity entity = dbServer.getDB().find(AssociationEntity.class).where().eq(AssociationEntity.ID, id).findOne();
        return mapStructMapper.toAssociationModelReturn(entity);
    }

    @Transactional
    public GuidResultModel save(AssociationModelAdd model) {
        GuidResultModel resultModel;
        AssociationEntity entity = mapStructMapper.toAssociationEntity(model);
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
    public AssociationModelReturn update(AssociationModelReturn model) {
        AssociationModelReturn resultModel;
        AssociationEntity entity = mapStructMapper.toAssociationEntity(model);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            resultModel = new AssociationModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        resultModel = model;
        return resultModel;
    }

    @Transactional
    public GeneralResultModel delete(UUID id) {
        GeneralResultModel resultModel;
        try {
            dbServer.getDB().find(AssociationEntity.class).where().eq(AssociationEntity.ID, id).delete();
        } catch (Exception e) {
            resultModel = new GeneralResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
            return resultModel;
        }
        resultModel = new GeneralResultModel();
        return resultModel;
    }

    @Transactional
    public List<AssociationModelReturn> saveList(List<AssociationModelAdd> modelAddList) {
        List<AssociationModelReturn> resultModel = new ArrayList<>();
        List<AssociationEntity> entities = mapStructMapper.toAssociationEntityList(modelAddList);
        try {
            dbServer.getDB().saveAll(entities);
        } catch (Exception e) {
            resultModel.add(new AssociationModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage()));
            return resultModel;
        }
        for (AssociationEntity entity : entities) {
            resultModel.add(mapStructMapper.toAssociationModelReturn(entity));
        }
        return resultModel;
    }

//    public List<String> findExist(List<String> name) {
//        return mapStructMapper.toAssociationModelReturn(dbServer.getDB()
//                .find(AssociationEntity.class)
//                .where()
//                .in(AssociationEntity., name)
//                .findList()).stream().map(WordModelReturn::getWord).collect(Collectors.toList());
//    }

    public PageResult<AssociationModelReturn> getPage(AssociationCriteriaModel criteriaModel) {
        PagedList<AssociationEntity> entityPagedList = createExpression(criteriaModel, dbServer.getDB().find(AssociationEntity.class).setFirstRow(criteriaModel.getSize() * (criteriaModel.getPageNumber() - 1)).setMaxRows(criteriaModel.getSize()).where()).findPagedList();
        return new PageResult<>(mapStructMapper.toAssociationModelList(entityPagedList.getList()), entityPagedList.getTotalCount());
    }

    private ExpressionList<AssociationEntity> createExpression(AssociationCriteriaModel criteriaModel, ExpressionList<AssociationEntity> expr) {
        if (criteriaModel.getWordIdFilter() != null) {
            expr.eq(AssociationEntity.WORD, criteriaModel.getWordIdFilter());
        }
        if (criteriaModel.getTranslationIdFilter() != null) {
            expr.eq(AssociationEntity.TRANSLATION, criteriaModel.getTranslationIdFilter());
        }
        if (criteriaModel.getAnyWordIdFilter() != null) {
            expr.or().eq(AssociationEntity.WORD, criteriaModel.getAnyWordIdFilter()).eq(AssociationEntity.TRANSLATION, criteriaModel.getAnyWordIdFilter()).endOr();
        }
        if (StringUtils.isNotBlank(criteriaModel.getWordFilter())) {
            var words = dbServer.getDB().find(WordEntity.class).where().like(WordEntity.WORD, escape(criteriaModel.getWordFilter(), '%')).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.WORD, words);
            }
        }
        if (StringUtils.isNotBlank(criteriaModel.getTranslationFilter())) {
            var words = dbServer.getDB().find(WordEntity.class).where().like(WordEntity.WORD, escape(criteriaModel.getTranslationFilter(), '%')).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.TRANSLATION, words);
            }
        }
        if (StringUtils.isNotBlank(criteriaModel.getAnyWordFilter())) {
            var words = dbServer.getDB().find(WordEntity.class).where().like(WordEntity.WORD, escape(criteriaModel.getAnyWordFilter(), '%')).findIds();
            if (!words.isEmpty()) {
                expr.or().in(AssociationEntity.WORD, words).in(AssociationEntity.TRANSLATION, words);
            }
        }
        if (criteriaModel.getWordLanguageIdFilter() != null) {
            var words = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.LANGUAGE, criteriaModel.getWordLanguageIdFilter()).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.WORD, words);
            }
        }
        if (criteriaModel.getTranslationLanguageIdFilter() != null) {
            var words = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.LANGUAGE, criteriaModel.getTranslationLanguageIdFilter()).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.TRANSLATION, words);
            }
        }
        if (criteriaModel.getAnyLanguageIdFilter() != null) {
            var words = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.LANGUAGE, criteriaModel.getAnyLanguageIdFilter()).findIds();
            if (!words.isEmpty()) {
                expr.or().in(AssociationEntity.WORD, words).in(AssociationEntity.TRANSLATION, words);
            }
        }
        if (StringUtils.isNotBlank(criteriaModel.getWordLanguageNameFilter())) {
            var languages = dbServer.getDB().find(LanguageEntity.class).where().like(LanguageEntity.NAME, escape(criteriaModel.getWordLanguageNameFilter(), '%')).findIds();
            var words = dbServer.getDB().find(WordEntity.class).where().in(WordEntity.LANGUAGE, languages).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.WORD, words);
            }
        }
        if (StringUtils.isNotBlank(criteriaModel.getTranslationLanguageNameFilter())) {
            var languages = dbServer.getDB().find(LanguageEntity.class).where().like(LanguageEntity.NAME, escape(criteriaModel.getTranslationLanguageNameFilter(), '%')).findIds();
            var words = dbServer.getDB().find(WordEntity.class).where().in(WordEntity.LANGUAGE, languages).findIds();
            if (!words.isEmpty()) {
                expr.in(AssociationEntity.TRANSLATION, words);
            }
        }
        if (StringUtils.isNotBlank(criteriaModel.getAnyLanguageFilter())) {
            var languages = dbServer.getDB().find(LanguageEntity.class).where().like(LanguageEntity.NAME, escape(criteriaModel.getAnyLanguageFilter(), '%')).findIds();
            var words = dbServer.getDB().find(WordEntity.class).where().in(WordEntity.LANGUAGE, languages).findIds();
            if (!words.isEmpty()) {
                expr.or().in(AssociationEntity.WORD, words).in(AssociationEntity.TRANSLATION, words);
            }
        }
        if(criteriaModel.getCreatedByUUID()!=null){
            expr.in(AssociationEntity.CREATED_BY_USER_ID,criteriaModel.getCreatedByUUID());
        }
        if (criteriaModel.getFromFilter() != null) {
            expr.ge(AssociationEntity.CREATED_AT, criteriaModel.getFromFilter());
        }
        if (criteriaModel.getToFilter() != null) {
            expr.le(AssociationEntity.CREATED_AT, criteriaModel.getToFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getSortFilter())) {
            expr.orderBy(criteriaModel.getSortFilter());
        }
        return expr;
    }

    public boolean exists(UUID association) {
        return dbServer.getDB().find(AssociationEntity.class).where().eq(AssociationEntity.ID, association).exists();
    }

    private String escape(String string, char esc) {
        return esc + string + esc;
    }

    public List<UUID> getAllAssociations(UUID word) {
        return dbServer.getDB().find(AssociationEntity.class).select(AssociationEntity.TRANSLATION).where().eq(AssociationEntity.WORD, word).findSingleAttributeList();
    }

    public AssociationModelReturnEnriched getByIdEnriched(UUID id) {
        AssociationEntity associationEntity = dbServer.getDB().find(AssociationEntity.class).where().eq(AssociationEntity.ID, id).findOne();
        if (associationEntity == null) {
            return null;
        }
        WordEntity wordEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, associationEntity.getWord()).findOne();
        WordEntity translationEntity = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.ID, associationEntity.getTranslation()).findOne();
        LanguageEntity wordLanguageEntity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.ID, wordEntity.getLanguageId()).findOne();
        LanguageEntity translationLanguageEntity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.ID, translationEntity.getLanguageId()).findOne();
        return mapStructMapper.toAssociationModelReturnEnriched(associationEntity, wordEntity, translationEntity, wordLanguageEntity, translationLanguageEntity);
    }

    public PageResult<WordModelReturn> translate(TranslationRequest request) {
        LanguageEntity wordLanguageEntity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.NAME, request.getWordLanguage()).findOne();
        LanguageEntity translationLanguageEntity = dbServer.getDB().find(LanguageEntity.class).where().eq(LanguageEntity.NAME, request.getTranslationLanguage()).findOne();
        WordEntity word = dbServer.getDB().find(WordEntity.class).where().eq(WordEntity.WORD, request.getWord()).eq(WordEntity.LANGUAGE, wordLanguageEntity.getId()).findOne();
        var translations = dbServer.getDB().find(AssociationEntity.class).select(AssociationEntity.TRANSLATION).where().eq(AssociationEntity.WORD, word.getId()).findSingleAttributeList();
        PagedList<WordEntity> entityPagedList = dbServer.getDB().find(WordEntity.class).setFirstRow(request.getSize() * (request.getPageNumber() - 1)).setMaxRows(request.getSize()).where().in(WordEntity.ID, translations).eq(WordEntity.LANGUAGE, translationLanguageEntity.getId()).findPagedList();
        return new PageResult<>(wordMapper.toWordModelReturnList(entityPagedList.getList()), entityPagedList.getTotalCount());
    }

}
