package com.example.demo.domain.export;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportCriteriaModel extends PageModel {
    private ExportType exportType;

    private LocalDateTime fromFilter;
    private LocalDateTime toFilter;

    private String addByLoginFilter;
    private String addByRoleFilter;
    private String addByFullNameFilter;
}
