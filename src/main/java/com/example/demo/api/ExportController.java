package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domainservices.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/export")
@Tag(name = "Export", description = "Data export APIs")
public class ExportController {
    private static final String FILE_IS_EMPTY_ERROR_CODE = "FILE_IS_EMPTY_ERROR_CODE";
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Result file is empty";
    @Inject
    private ExportService service;

    @PostMapping()
    //@PreAuthorize("isAuthenticated()")
    @Operation(summary = "Export data", description = "Export data to .xlsx file")
    public ResponseEntity<?> export(@Valid @RequestBody ExportCriteriaModel criteriaModel) throws IOException {
        var exportReturnModel = service.getFile(criteriaModel);
        var headers = new HttpHeaders();
        if (exportReturnModel.getErrorCode() != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(new GeneralResultModel(exportReturnModel.getErrorCode(), exportReturnModel.getErrorMessage()));
        }

        headers.set("Content-type", "application/octet-stream");
        headers.set("Content-Disposition", "attachment; filename=" + exportReturnModel.getFileName() + exportReturnModel.getFileExtension());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(exportReturnModel.getFileBody());
    }

}
