package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import com.example.demo.infrastructure.repositories.job.JobRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.ebeaninternal.server.util.Str;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@Scope("prototype")
public class TestJobImpl implements JobInterface{

    private UUID id;
    private Params jobParams;

    @Autowired
    private JobService service;

    @Override
    public void run() {
        log.info(jobParams.name + " : I am working!!!");
        log.info(jobParams.name + " : I am working!!!");
        log.info(jobParams.name + " : I am working!!!");
        log.info(jobParams.name + " : I am working!!!");
        log.info(jobParams.name + " : I am working!!!");
        log.info(jobParams.name + " : end(((");
        service.makeSuccess(id);
    }

    @Override
    public TaskType getType() {
        return TaskType.TEST_TASK;
    }

    @Override
    public void setParams(String params, UUID id) throws JsonProcessingException {
        jobParams=JsonUtils.fromJson(params, Params.class);
    }
    @Data
    private static class Params{
        @JsonProperty("name")
        private String name;
    }
}
