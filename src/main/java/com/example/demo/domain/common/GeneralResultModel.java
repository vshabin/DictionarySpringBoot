package com.example.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResultModel {
    private String errorCode;
    private String errorMessage;
}
