package com.example.demo.infrastructure.repositories.job;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.JobMapper;
import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.UUID;

@Repository
public class JobRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";
    @Inject
    DbServer dbServer;
    @Inject
    JobMapper mapStructMapper;

    public JobModelReturn findById(UUID id) {
        var entity = dbServer.getDB().find(JobEntity.class).where().eq(JobEntity.JOB_ID, id).findOne();
        return mapStructMapper.toJobModelReturn(entity);
    }
    @Transactional
    public GuidResultModel save(JobModelPost model) {
        var entity = mapStructMapper.toJobEntity(model);
        try {
            dbServer.getDB().save(entity);
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getJobId());
    }
}
