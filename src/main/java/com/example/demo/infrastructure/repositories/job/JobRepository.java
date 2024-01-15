package com.example.demo.infrastructure.repositories.job;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.JobMapper;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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

    public List<JobModelReturn> getUnsuccessfulJobs(){
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .ne(JobEntity.STATUS, TaskStatus.SUCCESS.name())
                .ne(JobEntity.STATUS, TaskStatus.IS_RUNNING.name())
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
    }

    @Transactional
    public List<JobModelReturn> updateList(List<JobModelReturn> jobs){
        var entities = mapStructMapper.toJobEntityList(jobs);
        var result = new ArrayList<JobModelReturn>();
        try {
            dbServer.getDB().updateAll(entities);
        } catch (Exception e) {
            result.add(new JobModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage()));
        }
        return result;
    }

    @Transactional
    public GuidResultModel makeSuccess(UUID id) {
        try {
            dbServer.getDB()
                    .update(JobEntity.class)
                    .set(JobEntity.STATUS, TaskStatus.SUCCESS)
                    .where()
                    .eq(JobEntity.JOB_ID, id)
                    .update();
        } catch (Exception e) {
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(id);
    }
}
