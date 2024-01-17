package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.TestParams;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class TestJobImpl implements JobInterface {

    private final Duration DELAY_AFTER_FAIL = Duration.ofMinutes(5);

    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    public void run(JobModelReturn jobModel) {
        var allProgress = 1;
        var successProgress = 0;
        var errorProgress = 0;
        try {
            var jobParams = JsonUtils.fromJson(jobModel.getParams(), TestParams.class);
//            Optional.ofNullable(jobParams).map().orElseThrow()
//            TimeUnit.MILLISECONDS.sleep(10000);
//            throw new Exception("Случилась ошибочка");
            for (int i = 0; i < jobParams.getWorkCount(); i++) {
                log.info(MessageFormat.format("Джоб работает id= {0}, попытка {1}, тип {2}, параметры: {3}  {4} ", jobModel.getJobId(), jobModel.getAttemptNum(), jobModel.getTaskType(), JsonUtils.toJson(jobParams), i));
                TimeUnit.MILLISECONDS.sleep(2000);
                if (i == 5) {
                    TestParams test = null;
                    test.getWorkCount();
                }
            }

            successProgress++;
            jobModel.setStatus(TaskStatus.SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jobModel.setTaskErrorMessage(e.getMessage());
            jobModel.setStatus(TaskStatus.FAILED);
            jobModel.setMinStartTime(LocalDateTime.now().plus(DELAY_AFTER_FAIL));
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

}
