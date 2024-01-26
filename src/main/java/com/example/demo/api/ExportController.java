package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domainservices.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.UUID;


@RestController
@RequestMapping("/export")
@Tag(name = "Export", description = "Data export APIs")
public class ExportController {
    @Inject
    private ExportService service;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Export data", description = "Export data to .xlsx file")
    public GuidResultModel export(@Valid @RequestBody ExportCriteriaModel criteriaModel) {
        return service.export(criteriaModel);
    }

    @GetMapping(value = "/get/{jobId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFile(@PathVariable UUID jobId) {
        var exportReturnModel = service.getFile(jobId);
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
