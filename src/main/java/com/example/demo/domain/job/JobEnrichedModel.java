package com.example.demo.domain.job;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.security.SecurityConst.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class JobEnrichedModel extends GeneralResultModel {
    private UUID jobId;
    private TaskType taskType;
    private UUID creatorUserId;
    private String creatorLogin;
    private Role creatorRole;
    private TaskStatus status;
    private String taskErrorMessage;
    private int attemptNum;
    private String progressMessage;
    private String progress;
    private String params;
    private String processor;
    private String context;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime minStartTime;
    private LocalDateTime createdAt;

    public JobEnrichedModel(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    public JobEnrichedModel(JobModelReturn jobModelReturn, String creatorLogin, Role creatorRole) {
        this.jobId = jobModelReturn.getJobId();
        this.taskType = jobModelReturn.getTaskType();
        this.creatorUserId = jobModelReturn.getCreatorUserId();
        this.creatorLogin = creatorLogin;
        this.creatorRole = creatorRole;
        this.status = jobModelReturn.getStatus();
        this.taskErrorMessage = jobModelReturn.getTaskErrorMessage();
        this.attemptNum = jobModelReturn.getAttemptNum();
        this.progressMessage = jobModelReturn.getProgressMessage();
        this.progress = jobModelReturn.getProgress();
        this.params = jobModelReturn.getParams();
        this.processor = jobModelReturn.getProcessor();
        this.context = jobModelReturn.getContext();
        this.lastUpdateTime = jobModelReturn.getLastUpdateTime();
        this.minStartTime = jobModelReturn.getMinStartTime();
        this.createdAt = jobModelReturn.getCreatedAt();
        this.setErrorCode(jobModelReturn.getErrorCode());
        this.setErrorMessage(jobModelReturn.getErrorMessage());
    }
}
