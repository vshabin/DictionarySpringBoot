package com.example.demo.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageModel {
    @Schema(name = "Count of elements on page", example = "5", required = true)
    private Integer size;
    @Schema(name = "Number of page", example = "1", required = true)
    private Integer pageNumber;
}
