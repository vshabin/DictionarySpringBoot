package com.example.demo.domain.job;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class JobModelReturn extends GeneralResultModel {
    private UUID jobId;
    private TaskType taskType;
    private UUID creatorUserId;
    private Status status;
    private String errorMessage;
    private int attemptNum;
    private String progressMessage;
    private String progress;
    private String params;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime minStartTime;
    private LocalDateTime createdAt;
}
