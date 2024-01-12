package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.job.TaskType;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestJobImpl implements JobInterface{
    @Override
    public void doTask() {
        log.info("I am working!!!");
    }

    @Override
    public TaskType getType() {
        return TaskType.TEST_TASK;
    }
}
