package com.example.demo.infrastructure.repositories.job;

import com.example.demo.config.ProcessorInfo;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapperInterfaces.JobMapper;
import io.ebean.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Log4j2
public class JobRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";
    @Autowired
    private DbServer dbServer;
    @Autowired
    private JobMapper mapStructMapper;
    @Autowired
    private ProcessorInfo processorInfo;

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
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
            return new JobModelReturn(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return model;
    }

    public List<JobModelReturn> getUnfinishedJobs() {
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .in(JobEntity.STATUS, TaskStatus.FAILED, TaskStatus.IS_RUNNING)
                .or()
                .isNull(JobEntity.MIN_START_TIME)
                .le(JobEntity.MIN_START_TIME, LocalDateTime.now())
                .endOr()
                .setMaxRows(100)
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
    }

    public List<JobModelReturn> getIsRunningOfProcessor() {
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .eq(JobEntity.STATUS, TaskStatus.IS_RUNNING)
                .eq(JobEntity.PROCESSOR, processorInfo.getComputerName())
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
    }

    public List<JobModelReturn> getAllIsRunning() {
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .eq(JobEntity.STATUS, TaskStatus.IS_RUNNING)
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
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

    public boolean getIsCanceled(UUID id) {
        var entity = dbServer.getDB().find(JobEntity.class).where().eq(JobEntity.JOB_ID, id).findOne();
        if (entity == null) {
            return false;
        }
        return entity.getStatus().equals(TaskStatus.CANCELED);
    }

    @Transactional
    public GuidResultModel cancel(UUID id) {
        try {
            dbServer.getDB()
                    .find(JobEntity.class)
                    .where()
                    .eq(JobEntity.JOB_ID, id)
                    .in(JobEntity.STATUS, TaskStatus.FAILED, TaskStatus.IS_RUNNING, TaskStatus.NEW)
                    .asUpdate()
                    .set(JobEntity.STATUS, TaskStatus.CANCELED)
                    .update();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(id);
    }

    public boolean thereIsSameTask(TaskType taskName, LocalDateTime next) {
        return dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .eq(JobEntity.TASK_TYPE, taskName)
                .eq(JobEntity.MIN_START_TIME, next)
                .exists();
    }

//      ДЛЯ ПОТОМКОВ
//    public List<JobModelReturn> getJobsForExecuteExcept(int threadCount, List<TaskType> except) {
//        var req = "SELECT \"jobId\", \"taskType\", \"creatorUserId\", status, \"errorMessage\", \"attemptNum\", \"progressMessage\", progress, params, \"lastUpdateTime\", \"minStartTime\", created_at, processor FROM (\n" +
//                "SELECT *, row_number() over (PARTITION BY \"taskType\", \"creatorUserId\" order by created_at) as row\n" +
//                "FROM jobs\n" +
//                "WHERE status = 'NEW'\n" +
//                "OR status = 'FAILED'\n" +
//                "AND \"taskType\" IN (:except)\n" +
//                "and (\"taskType\", \"creatorUserId\") not in (SELECT \"taskType\", \"creatorUserId\"\n" +
//                "FROM jobs\n" +
//                "WHERE status = 'IS_RUNNING'\n" +
//                "AND \"taskType\" IN (:except)\n" +
//                "GROUP BY \"taskType\", \"creatorUserId\")) as a\n" +
//                "WHERE row = 1\n" +
//                "\n" +
//                "UNION\n" +
//                "\n" +
//                "SELECT * FROM jobs\n" +
//                "WHERE (status = 'NEW' OR status = 'FAILED') AND \"taskType\" NOT IN (:except)";
//        var rows = dbServer.getDB()
//                .sqlQuery(req)
//                .setParameter("except", except)
//                .setMaxRows(threadCount)
//                .findList();
//        List<JobModelReturn> result = new ArrayList<>();
//        rows.forEach(row -> {
//            LocalDateTime minStartTime;
//            if (row.get(JobEntity.MIN_START_TIME) != null) {
//                minStartTime = ((Timestamp) row.get(JobEntity.LAST_UPDATE_TIME)).toLocalDateTime();
//            } else {
//                minStartTime = null;
//            }
//            result.add(new JobModelReturn(
//                    (UUID) row.get(JobEntity.JOB_ID),
//                    TaskType.valueOf((String) row.get(JobEntity.TASK_TYPE)),
//                    (UUID) row.get(JobEntity.CREATOR_USER_ID),
//                    TaskStatus.valueOf((String) row.get(JobEntity.STATUS)),
//                    (String) row.get(JobEntity.ERROR_MESSAGE),
//                    (Integer) row.get(JobEntity.ATTEMPT_NUM),
//                    (String) row.get(JobEntity.PROGRESS_MESSAGE),
//                    (String) row.get(JobEntity.PROGRESS),
//                    (String) row.get(JobEntity.PARAMS),
//                    (String) row.get(JobEntity.PROCESSOR),
//                    ((Timestamp) row.get(JobEntity.LAST_UPDATE_TIME)).toLocalDateTime(),
//                    minStartTime,
//                    ((Timestamp) row.get(JobEntity.CREATED_AT)).toLocalDateTime()
//
//            ));
//        });
//        return result;
//    }

    public List<JobModelReturn> getJobsForExecuteExcept(int threadCount) {
        var req = "SELECT \""+JobEntity.JOB_ID+"\", \""+JobEntity.TASK_TYPE+"\", \""+JobEntity.CREATOR_USER_ID+"\", "+JobEntity.STATUS+", \""+JobEntity.ERROR_MESSAGE+"\", \""+JobEntity.ATTEMPT_NUM+"\", \""+JobEntity.PROGRESS_MESSAGE+"\", "+JobEntity.PROGRESS+", "+JobEntity.PARAMS+", \""+JobEntity.LAST_UPDATE_TIME+"\", \""+JobEntity.MIN_START_TIME+"\", "+JobEntity.CREATED_AT+", "+JobEntity.PROCESSOR+", "+JobEntity.CONTEXT+" FROM (\n" +
                "SELECT *, row_number() over (PARTITION BY \""+JobEntity.CONTEXT+"\" order by "+JobEntity.CREATED_AT+") as row\n" +
                "FROM jobs\n" +
                "WHERE "+JobEntity.STATUS+" IN('"+TaskStatus.NEW+"', '"+TaskStatus.FAILED+"')\n" +
                "AND "+JobEntity.CONTEXT+" IS NOT NULL\n" +
                "and \""+JobEntity.CONTEXT+"\" not in (SELECT \""+JobEntity.CONTEXT+"\"\n" +
                "FROM jobs\n" +
                "WHERE "+JobEntity.STATUS+" = '"+TaskStatus.IS_RUNNING+"')) as b\n" +
                "WHERE row = 1\n" +
                "\n" +
                "UNION\n" +
                "\n" +
                "SELECT * FROM jobs \n" +
                "WHERE "+JobEntity.CONTEXT+" IS NULL\n" +
                "AND "+JobEntity.STATUS+" IN('"+TaskStatus.NEW+"', '"+TaskStatus.FAILED+"')";


        var rows = dbServer.getDB()
                .sqlQuery(req)
                .setMaxRows(threadCount)
                .findList();
        List<JobModelReturn> result = new ArrayList<>();
        rows.forEach(row -> {
            LocalDateTime minStartTime;
            if (row.get(JobEntity.MIN_START_TIME) != null) {
                minStartTime = ((Timestamp) row.get(JobEntity.LAST_UPDATE_TIME)).toLocalDateTime();
            } else {
                minStartTime = null;
            }
            result.add(new JobModelReturn(
                    (UUID) row.get(JobEntity.JOB_ID),
                    TaskType.valueOf((String) row.get(JobEntity.TASK_TYPE)),
                    (UUID) row.get(JobEntity.CREATOR_USER_ID),
                    TaskStatus.valueOf((String) row.get(JobEntity.STATUS)),
                    (String) row.get(JobEntity.ERROR_MESSAGE),
                    (Integer) row.get(JobEntity.ATTEMPT_NUM),
                    (String) row.get(JobEntity.PROGRESS_MESSAGE),
                    (String) row.get(JobEntity.PROGRESS),
                    (String) row.get(JobEntity.PARAMS),
                    (String) row.get(JobEntity.PROCESSOR),
                    (String) row.get(JobEntity.CONTEXT),
                    ((Timestamp) row.get(JobEntity.LAST_UPDATE_TIME)).toLocalDateTime(),
                    minStartTime,
                    ((Timestamp) row.get(JobEntity.CREATED_AT)).toLocalDateTime()
            ));
        });
        return result;
    }

    public List<JobModelReturn> getJobsByUserId(UUID userId) {
        var result = dbServer.getDB()
                .find(JobEntity.class)
                .where()
                .in(JobEntity.STATUS, TaskStatus.FAILED, TaskStatus.IS_RUNNING)
                .eq(JobEntity.CREATOR_USER_ID, userId)
                .findList();
        return mapStructMapper.toJobModelReturnList(result);
    }
}
