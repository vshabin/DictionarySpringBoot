package com.example.demo.api;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domainservices.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("/import")
@Tag(name = "Import", description = "Data import APIs")
public class ImportController {

    @Inject
    private ImportService service;

    @PostMapping(value = "/dictionary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Import dictionary", description = "Upload data from .xlsx file")
    public ResponseEntity<?> importFile (@RequestBody MultipartFile file) throws IOException {
        var importReturnModel = service.readFile(ImportType.DICTIONARY_IMPORT, file);
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
