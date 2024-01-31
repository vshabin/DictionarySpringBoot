package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskType;

public interface JobInterface {
    int getMaxAttempt();

    void run(JobModelReturn job);

    TaskType getType();

}
