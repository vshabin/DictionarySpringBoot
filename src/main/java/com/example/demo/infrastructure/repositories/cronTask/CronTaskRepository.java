package com.example.demo.infrastructure.repositories.cronTask;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.crontasks.CronTaskPostModel;
import com.example.demo.domain.crontasks.CronTaskReturnModel;
import com.example.demo.infrastructure.repositories.DbServer;
import com.example.demo.infrastructure.repositories.MapperInterfaces.CronTaskMapper;
import io.ebean.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Repository
@Log4j2
public class CronTaskRepository {
    private final String DATABASE_TRANSACTION_ERROR_CODE = "DATABASE_TRANSACTION_ERROR";
    private final String DATABASE_TRANSACTION_ERROR_MESSAGE = "Ошибка проведения транзакции: ";
    @Inject
    DbServer dbServer;
    @Inject
    CronTaskMapper mapStructMapper;

    public CronTaskReturnModel findById(UUID id) {
        var entity = dbServer.getDB()
                .find(CronTaskEntity.class)
                .where()
                .eq(CronTaskEntity.TASK_ID, id)
                .findOne();
        return mapStructMapper.toCronTaskModelReturn(entity);
    }

    @Transactional
    public GuidResultModel save(CronTaskPostModel model) {
        var entity = mapStructMapper.toCronTaskEntity(model);
        try {
            dbServer.getDB()
                    .save(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new GuidResultModel(DATABASE_TRANSACTION_ERROR_CODE, DATABASE_TRANSACTION_ERROR_MESSAGE + e.getMessage());
        }
        return new GuidResultModel(entity.getTaskId());
    }

    public List<CronTaskReturnModel> getTasks() {
        var result = dbServer.getDB()
                .find(CronTaskEntity.class)
                .findList();
        return mapStructMapper.toCronTaskModelReturnList(result);
    }
}
