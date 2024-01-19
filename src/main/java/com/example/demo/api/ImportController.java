package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domain.job.params.ImportParams;
import com.example.demo.domainservices.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/import")
@Tag(name = "Import", description = "Data import APIs")
public class ImportController {
    @Inject
    private ImportService service;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Import data", description = "Upload data from .xlsx file")
    public GuidResultModel importFile(@RequestBody MultipartFile file, ImportType type){
        return service.importFile(file,type);
    }

    @GetMapping(value = "/get/{jobId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFile(@PathVariable UUID jobId){
        var importReturnModel = service.getFile(jobId);
        var headers = new HttpHeaders();
        if (importReturnModel.getErrorCode() != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body(new GeneralResultModel(importReturnModel.getErrorCode(), importReturnModel.getErrorMessage()));
        }

        headers.set("Content-type", "application/octet-stream");
        headers.set("Content-Disposition", "attachment; filename=" + importReturnModel.getFileName() + importReturnModel.getFileExtension());
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(importReturnModel.getFileBody());
    }
}
