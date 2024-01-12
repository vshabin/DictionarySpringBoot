package com.example.demo.domain.job;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class JobModelPost extends GeneralResultModel {
    private TaskType taskType;
    private String params;
    private LocalDateTime minStartTime;
}
