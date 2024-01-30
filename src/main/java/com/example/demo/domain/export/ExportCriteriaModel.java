package com.example.demo.domain.export;

import com.example.demo.domain.common.PageModel;
import io.ebean.annotation.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class ExportCriteriaModel extends PageModel {
    @NotNull
    private ExportType exportType;
    @NotEmpty
    private String fileExtension;
    private boolean sendEmail;

    private LocalDateTime fromFilter;
    private LocalDateTime toFilter;

    private String byLoginFilter;
    private String byRoleFilter;
    private String byFullNameFilter;
}
