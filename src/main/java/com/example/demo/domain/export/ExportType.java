package com.example.demo.domain.export;

import com.example.demo.domain.job.TaskType;
import lombok.Getter;

public enum ExportType {
    ASSOCIATIONS_EXPORT(TaskType.ASSOCIATIONS_EXPORT),
    USER_EXPORT(TaskType.USER_EXPORT),


    ;

    @Getter
    private final TaskType jobType;
    ExportType(TaskType jobType) {
        this.jobType = jobType;
    }
}
