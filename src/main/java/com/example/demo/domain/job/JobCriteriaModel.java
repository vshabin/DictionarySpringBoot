package com.example.demo.domain.job;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class JobCriteriaModel extends PageModel {
    private TaskType taskTypeFilter;

    private UUID creatorUserIDFilter;
    private String creatorLoginFilter;

    private TaskStatus taskStatusFilter;

    private String processorFilter;

    private LocalDateTime createdFromFilter;
    private LocalDateTime createdToFilter;
    private String sortFilter;
}
