package com.example.demo.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageModel {
    @Schema(name = "Count of elements on page", example = "5", required = true)
    @Min(value = 1, message = "Количество элементов на странице должно быть больше 0")
    private Integer size;
    @Schema(name = "Number of page", example = "1", required = true)
    @Min(value = 1, message = "Номер страницы должен быть больше 0")
    private Integer pageNumber;
}
