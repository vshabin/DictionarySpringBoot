package com.example.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> extends GeneralResultModel {
    private List<T> pageContent;
    private Integer totalCount;

    public PageResult(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
