package com.example.demo.domain.job;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JobModelReturn extends GeneralResultModel {
    private UUID jobId;
    private TaskType taskType;
    private UUID creatorUserId;
    private TaskStatus status;
    private String taskErrorMessage;
    private int attemptNum;
    private String progressMessage;
    private String progress;
    private String params;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime minStartTime;
    private LocalDateTime createdAt;

    public JobModelReturn(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
