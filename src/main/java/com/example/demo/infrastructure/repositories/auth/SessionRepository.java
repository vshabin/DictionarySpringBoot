package com.example.demo.infrastructure.repositories.auth;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.LanguageMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.UUID;
import java.util.Date;

@Repository
public class SessionRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";

    @Inject
    DbServer dbServer;
    @Transactional
    public GuidResultModel save(UUID id, String refreshToken, Date refreshExpiresAt, String accessToken, Date accessTokenExpiresAt, UUID userId) {
        SessionEntity entity = new SessionEntity(id, refreshToken, refreshExpiresAt, accessToken, accessTokenExpiresAt, userId);
        try{
            dbServer.getDB().save(entity);
        }
        catch (Exception e){
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE,DATABASE_TRANSACTION_ERROR_MESSAGE);
        }
        return new GuidResultModel(entity.getId());
    }

    public boolean exists(String refreshToken){
        return  dbServer.getDB()
                .find(SessionEntity.class)
                .where()
                .eq(SessionEntity.REFRESH_TOKEN, refreshToken)
                .exists();
    }

    public GeneralResultModel delete(String refreshToken) {
        try {
            dbServer.getDB().find(SessionEntity.class)
                    .where()
                    .eq(SessionEntity.REFRESH_TOKEN, refreshToken)
                    .delete();
        } catch (Exception e) {
            return new GeneralResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return null;
    }
    @Transactional
    public GeneralResultModel update(UUID id,String refreshToken, Date refreshExpiresAt, String accessToken, Date accessTokenExpiresAt){
        try {
            dbServer.getDB().update(SessionEntity.class)
                    .set(SessionEntity.REFRESH_TOKEN,refreshToken)
                    .set(SessionEntity.REFRESH_EXPIRES_AT,refreshExpiresAt)
                    .set(SessionEntity.ACCESS_TOKEN,accessToken)
                    .set(SessionEntity.ACCESS_EXPIRES_AT,accessTokenExpiresAt)
                    .where()
                    .eq(SessionEntity.ID, id)
                    .update();
        } catch (Exception e) {
            return new GeneralResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return null;
    }
}