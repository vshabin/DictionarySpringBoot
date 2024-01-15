package com.example.demo.infrastructure.repositories.job;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.JobMapper;
import io.ebean.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Log4j2
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
    public GuidResultModel save(JobModelReturn model) {
        var entity = mapStructMapper.toJobEntity(model);
        try {
            dbServer.getDB()
                    .save(entity);
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getJobId());
    }

    @Transactional
    public JobModelReturn update(JobModelReturn model) {
        JobEntity entity = mapStructMapper.toJobEntity(model);
        try {
            dbServer.getDB().update(entity);
        } catch (Exception e) {
            return new JobModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return model;
    }

    public List<JobModelReturn> getUnfinishedJobs() {
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .notIn(JobEntity.STATUS,TaskStatus.SUCCESS.name(), TaskStatus.IS_RUNNING.name(), TaskStatus.ATTEMPTS_ARE_OVER.name())
                .setMaxRows(100)
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
    }

    public List<JobModelReturn> getIsRunning() {

    }

    @Transactional
    public List<JobModelReturn> updateList(List<JobModelReturn> jobs) {
        var entities = mapStructMapper.toJobEntityList(jobs);
        var result = new ArrayList<JobModelReturn>();
        try {
            dbServer.getDB().updateAll(entities);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.add(new JobModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage()));
        }
        return result;
    }
}
