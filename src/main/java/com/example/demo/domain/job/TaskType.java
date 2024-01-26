package com.example.demo.domain.job;

import lombok.Getter;

public enum TaskType {
    DICTIONARY_IMPORT_EXCEL(false),
    ASSOCIATIONS_EXPORT(false),
    USER_EXPORT(true),
    TEST_TASK(true)


    ;

    @Getter
    private final boolean isParallelize;
    TaskType(boolean isParallelize) {
        this.isParallelize = isParallelize;
    }
}
