package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.UUID;

public interface JobInterface extends Runnable{
    TaskType getType();
    void setParams(String params, UUID id) throws JsonProcessingException;
}
