package com.example.demo.domain.association;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssociationCriteriaModel extends PageModel {
    @Schema(name = "Filter by word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID wordIdFilter;
    @Schema(name = "Filter by translation id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID translationIdFilter;

    @Schema(name = "Filter by word", example = "Соб", required = false)
    private String wordFilter;
    @Schema(name = "Filter by translation", example = "Соб", required = false)
    private String translationFilter;

    @Schema(name = "Filter by word language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID wordLanguageIdFilter;
    @Schema(name = "Filter by translation language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID translationLanguageIdFilter;

    @Schema(name = "Filter by word language name", example = "Jap", required = false)
    private String wordLanguageNameFilter;
    @Schema(name = "Filter by translation language name", example = "Jap", required = false)
    private String translationLanguageNameFilter;

    @Schema(name = "Filter by any word id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID anyWordIdFilter;
    @Schema(name = "Filter by any word", example = "Соб", required = false)
    private String anyWordFilter;
    @Schema(name = "Filter by any word language id", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", required = false)
    private UUID anyLanguageIdFilter;
    @Schema(name = "Filter by any word language name", example = "Jap", required = false)
    private String anyLanguageFilter;

    @Schema(name = "Timestamp after which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private LocalDateTime fromFilter;
    @Schema(name = "Timestamp before which the object was created", example = "2023-12-03 18:59:00.655995 +00:00", required = false)
    private LocalDateTime toFilter;
    @Schema(name = "Field sorting type", example = "name asc,id desc", required = false)
    private String sortFilter;
}