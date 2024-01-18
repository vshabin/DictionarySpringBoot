package com.example.demo.infrastructure.repositories.user;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelPost;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapperInterfaces.UserMapper;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private static final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR_CODE";
    private static final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";

    @Inject
    DbServer dbServer;
    @Inject
    UserMapper mapStructMapper;


    public UserModelReturn getById(UUID id) {
        UserEntity entity = dbServer.getDB().find(UserEntity.class).where().eq(UserEntity.ID, id).findOne();
        return mapStructMapper.toUserModelReturn(entity);
    }

    public UserModelReturn getByLogin(String login) {
        UserEntity entity = dbServer.getDB().find(UserEntity.class).where().eq(UserEntity.LOGIN, login).findOne();
        return mapStructMapper.toUserModelReturn(entity);
    }

    public boolean exists(String login) {
        return dbServer.getDB().find(UserEntity.class).where().eq(UserEntity.LOGIN, login).exists();
    }

    @Transactional
    public GuidResultModel save(UserModelPost model) {

        UserEntity entity = mapStructMapper.toUserEntity(model);
        try {
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getId());
    }

    public String getEncodedPassword(String login) {
        return dbServer.getDB().find(UserEntity.class).select(UserEntity.PASSWORD).where().eq(UserEntity.LOGIN, login).findSingleAttribute();
    }

    public List<UserModelReturn> getUserListByIdList(List<UUID> ids){
        return mapStructMapper.toUserModelReturnList(dbServer.getDB()
                .find(UserEntity.class)
                .where()
                .in(UserEntity.ID,ids)
                .findList());
    }

    public List<UserModelReturn> getFilteredList(UserCriteriaModel criteriaModel) {
        var entityList = createExpression(criteriaModel,
                dbServer.getDB()
                        .find(UserEntity.class)
                        .where())
                .findList();
        return mapStructMapper.toUserModelReturnList(entityList);
    }


    private ExpressionList<UserEntity> createExpression(UserCriteriaModel criteriaModel, ExpressionList<UserEntity> expr) {
        if (StringUtils.isNotBlank(criteriaModel.getRoleFilter())) {
            expr.like(UserEntity.ROLE, escape(criteriaModel.getRoleFilter(), '%'));
        }
        if (StringUtils.isNotBlank(criteriaModel.getLoginFilter())) {
            expr.like(UserEntity.LOGIN, escape(criteriaModel.getLoginFilter(), '%'));
        }
        if (StringUtils.isNotBlank(criteriaModel.getFullNameFilter())) {
            expr.like(UserEntity.FULL_NAME, escape(criteriaModel.getFullNameFilter(), '%'));
        }
        if (StringUtils.isNotBlank(criteriaModel.getSortFilter())) {
            expr.orderBy(criteriaModel.getSortFilter());
        }
        return expr;
    }

    private String escape(String string, char esc) {
        return esc + string + esc;
    }

    public PageResult<UserModelReturn> getPage(UserCriteriaModel userCriteriaModel) {
        PagedList<UserEntity> entityPagedList = createExpression(userCriteriaModel,
                dbServer.getDB()
                        .find(UserEntity.class)
                        .setFirstRow(userCriteriaModel.getSize() * (userCriteriaModel.getPageNumber() - 1))
                        .setMaxRows(userCriteriaModel.getSize()).where())
                .findPagedList();
        return new PageResult<>(mapStructMapper.toUserModelReturnList(entityPagedList.getList()), entityPagedList.getTotalCount());
    }
}
