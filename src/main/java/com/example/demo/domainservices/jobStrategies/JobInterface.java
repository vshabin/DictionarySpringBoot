package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;

public interface JobInterface {
    void doTask();
    TaskType getType();
}
