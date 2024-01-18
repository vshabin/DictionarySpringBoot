package com.example.demo.domain.crontasks;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.job.TaskType;
import lombok.Data;

@Data
public class CronTaskPostModel extends GeneralResultModel {
    private TaskType taskName;
    private String cronExpression;
}
