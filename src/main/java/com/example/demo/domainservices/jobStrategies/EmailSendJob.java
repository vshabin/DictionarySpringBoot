package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;

public class EmailSendJob extends BaseJob{
    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {

    }

    @Override
    public int getMaxAttempt() {
        return 0;
    }

    @Override
    public TaskType getType() {
        return null;
    }
}
