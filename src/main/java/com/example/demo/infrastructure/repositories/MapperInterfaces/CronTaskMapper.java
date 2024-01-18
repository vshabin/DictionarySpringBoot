package com.example.demo.infrastructure.repositories.MapperInterfaces;

import com.example.demo.domain.crontasks.CronTaskPostModel;
import com.example.demo.domain.crontasks.CronTaskReturnModel;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.infrastructure.repositories.cronTask.CronTaskEntity;
import com.example.demo.infrastructure.repositories.job.JobEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface CronTaskMapper {

    CronTaskReturnModel toCronTaskModelReturn(CronTaskEntity entity);

    CronTaskEntity toCronTaskEntity(CronTaskPostModel model);

    List<CronTaskReturnModel> toCronTaskModelReturnList(List<CronTaskEntity> result);
}
