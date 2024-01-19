package com.example.demo.domain.fileImport;

import com.example.demo.domain.job.TaskType;
import lombok.Getter;

public enum ImportType {
    DICTIONARY_IMPORT(TaskType.DICTIONARY_IMPORT_EXCEL),


    ;
    @Getter
    private final TaskType jobType;
    ImportType(TaskType jobType) {
        this.jobType = jobType;
    }
}
