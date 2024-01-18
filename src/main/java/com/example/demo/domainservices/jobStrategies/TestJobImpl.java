package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.exceptions.CancelException;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.exceptions.ErrorException;
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
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class TestJobImpl extends BaseJob {


    private static final String FAILED_READ_PARAMS_EXCEPTION_MESSAGE = "Failed to read parameters";

    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    public int getMaxAttempt() {
        return 5;
    }


    public void internalRun(JobModelReturn jobModel, ProgressMessageModel progressMessageModel) throws CriticalErrorException, CancelException, ErrorException {
        DELAY_AFTER_FAIL = Duration.ofMinutes(30);
        var jobParams = JsonUtils.fromJson(jobModel.getParams(), TestParams.class)
                .orElseThrow(() -> new CriticalErrorException(FAILED_READ_PARAMS_EXCEPTION_MESSAGE));

        progressMessageModel.setAllCount(jobParams.getWorkCount());

        for (int i = 0; i < jobParams.getWorkCount(); i++) {
            log.info(MessageFormat.format("Джоб работает id= {0}, попытка {1}, тип {2}, параметры: {3}  {4} ",
                    jobModel.getJobId(),
                    jobModel.getAttemptNum(),
                    jobModel.getTaskType(),
                    JsonUtils.toJson(jobParams),
                    i));
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (Exception e) {
                throw new ErrorException(e.getMessage());
            }

            if (jobService.getIsCanceled(jobModel.getJobId())) {
                throw new CancelException();
            }
            progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + 1);
            jobModel.setProgressMessage(JsonUtils.toJson(progressMessageModel));
            jobModel.setProgress(JsonUtils.toJson(progressMessageModel));
            jobService.update(jobModel);
        }

    }

    @Override
    public TaskType getType() {
        return TaskType.TEST_TASK;
    }

}
