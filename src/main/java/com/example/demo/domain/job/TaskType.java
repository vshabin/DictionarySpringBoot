package com.example.demo.domain.job;

import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.fileImport.ImportType;
import lombok.Getter;

public enum TaskType {
    DICTIONARY_IMPORT(ImportType.DICTIONARY_IMPORT),
    ASSOCIATIONS_EXPORT(ExportType.ASSOCIATIONS_EXPORT),
    USER_EXPORT(ExportType.USER_EXPORT),
    TEST_TASK(null)


    ;
    @Getter
    private final Enum type;
    TaskType(Enum type) {
        this.type = type;
    }
}
