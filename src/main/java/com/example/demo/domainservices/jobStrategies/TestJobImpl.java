package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@Scope("prototype")
public class TestJobImpl implements JobInterface {

    private JobModelReturn jobModel;
    private Params jobParams;

    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    public void run() {
        var allProgress = 1;
        var successProgress = 0;
        var errorProgress = 0;
        try {
            while (true) {
                log.info(MessageFormat.format("Джоб работает id= {0}, попытка {1}, тип {2}, параметры: {3}", jobModel.getJobId(), jobModel.getAttemptNum(), jobModel.getTaskType(),JsonUtils.toJson(jobParams)));
                TimeUnit.MILLISECONDS.sleep(2000);
            }
//            log.info(jobParams.name + " : I am working!!!");
//            log.info(jobParams.name + " : I am working!!!");
//            log.info(jobParams.name + " : I am working!!!");
//            log.info(jobParams.name + " : I am working!!!");
//            log.info(jobParams.name + " : I am working!!!");
//            log.info(jobParams.name + " : end(((");
//            //throw new Exception("Случилась ошибочка");
//
//            successProgress++;
//            model.setStatus(TaskStatus.SUCCESS);
        } catch (Exception e) {
            jobModel.setTaskErrorMessage(e.getMessage());
            jobModel.setStatus(TaskStatus.FAILED);
            jobService.update(jobModel);
            errorProgress++;
        }

        var progressMessageModel = new ProgressMessageModel(successProgress, errorProgress, allProgress);
        try {
            jobModel.setProgressMessage(JsonUtils.toJson(progressMessageModel));
            jobModel.setProgress(JsonUtils.toJson(progressMessageModel));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        jobService.update(jobModel);
    }

    @Override
    public TaskType getType() {
        return TaskType.TEST_TASK;
    }

    @Override
    public void setParams(JobModelReturn jobModel) throws JsonProcessingException {
        jobParams = JsonUtils.fromJson(jobModel.getParams(), Params.class);
        this.jobModel = jobModel;
    }

    @Data
    private static class Params {
        @JsonProperty("name")
        private String name;
    }
}
