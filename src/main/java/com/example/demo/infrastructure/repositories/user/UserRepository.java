package com.example.demo.infrastructure.repositories.user;

import com.example.demo.domain.common.GuidResultModel;

import com.example.demo.domain.user.UserModelPost;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.UserMapper;
import io.ebean.annotation.Transactional;
import io.ebeaninternal.server.util.Str;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
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
        UserEntity entity = dbServer.getDB()
                .find(UserEntity.class)
                .where()
                .eq(UserEntity.ID, id)
                .findOne();
        return mapStructMapper.toUserModelReturn(entity);
    }

    public UserModelReturn getByLogin(String login) {
        UserEntity entity = dbServer.getDB()
                .find(UserEntity.class)
                .where()
                .eq(UserEntity.LOGIN, login)
                .findOne();
        return mapStructMapper.toUserModelReturn(entity);
    }

    public boolean exists(String login) {
        return dbServer.getDB()
                .find(UserEntity.class)
                .where()
                .eq(UserEntity.LOGIN, login)
                .exists();
    }
    @Transactional
    public GuidResultModel save(UserModelPost model) {

        UserEntity entity = mapStructMapper.toUserEntity(model);
        try {
            dbServer.getDB()
                   .save(entity);
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getId());
    }

    public String getEncodedPassword(String login) {
        return dbServer.getDB()
                .find(UserEntity.class)
                .select(UserEntity.PASSWORD)
                .where()
                .eq(UserEntity.LOGIN,login)
                .findSingleAttribute();
    }
}
