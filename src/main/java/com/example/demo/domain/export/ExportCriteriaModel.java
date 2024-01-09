package com.example.demo.domain.export;

import com.example.demo.domain.common.PageModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportCriteriaModel extends PageModel {
    private ExportType exportType;

    private LocalDateTime fromFilter;
    private LocalDateTime toFilter;

    private String byLoginFilter;
    private String byRoleFilter;
    private String byFullNameFilter;
}
