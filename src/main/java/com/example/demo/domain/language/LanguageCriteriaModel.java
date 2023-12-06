package com.example.demo.domain.language;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LanguageCriteriaModel extends PageModel {
    @Schema(name = "Filter by name", example = "Eng", required = false)
    private String nameFilter;
    @Schema(name = "Timestamp after which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private LocalDateTime fromFilter;
    @Schema(name = "Timestamp before which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private LocalDateTime toFilter;
    @Schema(name = "Field sorting type", example = "name asc,id desc", required = false)
    private String sortFilter;
}
