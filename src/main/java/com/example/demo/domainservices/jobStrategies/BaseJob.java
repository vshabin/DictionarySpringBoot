package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.exceptions.CancelException;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.exceptions.ErrorException;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.time.LocalDateTime;

@Log4j2
public abstract class BaseJob implements JobInterface {
    public Duration DELAY_AFTER_FAIL = Duration.ofMinutes(5);

    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    public void run(JobModelReturn jobModel) {
        ProgressMessageModel progressMessageModel =JsonUtils.fromJson(jobModel.getProgressMessage(), ProgressMessageModel.class)
                .orElse(new ProgressMessageModel());
        try {
            internalRun(jobModel, progressMessageModel);
            jobModel.setStatus(TaskStatus.SUCCESS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jobModel.setTaskErrorMessage(e.getMessage());
            if (e instanceof ErrorException) {
                jobModel.setStatus(TaskStatus.FAILED);
                jobModel.setMinStartTime(LocalDateTime.now().plus(DELAY_AFTER_FAIL));
            } else if (e instanceof CriticalErrorException) {
                jobModel.setStatus(TaskStatus.CRITICAL_ERROR);
            } else if (e instanceof CancelException) {
                jobModel.setStatus(TaskStatus.CANCELED);
            } else {
                jobModel.setStatus(TaskStatus.CRITICAL_ERROR);
            }
            progressMessageModel.setErrorCount(progressMessageModel.getAllCount() - progressMessageModel.getSuccessCount());
            jobModel.setProgressMessage(JsonUtils.toJson(progressMessageModel));
        }
        jobService.update(jobModel);
    }
    protected abstract void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel);
}
