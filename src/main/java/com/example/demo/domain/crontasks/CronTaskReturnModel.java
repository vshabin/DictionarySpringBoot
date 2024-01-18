package com.example.demo.domain.crontasks;

import com.example.demo.domain.job.TaskType;
import lombok.Data;

import java.util.UUID;

@Data
public class CronTaskReturnModel {
    private UUID taskId;
    private TaskType taskName;
    private String cronExpression;
    private UUID creatorUserId;
}
