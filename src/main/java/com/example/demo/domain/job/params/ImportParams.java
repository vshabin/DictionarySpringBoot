package com.example.demo.domain.job.params;

import com.example.demo.domain.fileImport.ImportType;
import io.ebean.annotation.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ImportParams {
    @NotEmpty
    private String fileExtension;
}
