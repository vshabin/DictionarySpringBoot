package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskType;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface JobInterface extends Runnable {
    TaskType getType();

    void setParams(JobModelReturn jobModel) throws JsonProcessingException;
}
