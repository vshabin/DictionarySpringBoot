package com.example.demo.domain.word;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class WordCriteriaModel extends PageModel {
    @Schema(name = "Filter by word", example = "Соб", required = false)
    private String wordFilter;
    @Schema(name = "Filter by language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID langIdFilter;
    @Schema(name = "Filter by language name", example = "Jap", required = false)
    private String langNameFilter;
    @Schema(name = "Timestamp after which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private Date fromFilter;
    @Schema(name = "Timestamp before which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private Date toFilter;
    @Schema(name = "Field sorting type", example = "name asc,id desc", required = false)
    private String sortFilter;
}
