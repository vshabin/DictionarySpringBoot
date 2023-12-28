package com.example.demo.domainservices;

import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ExportInterface {
    ExportReturnModel getFile(ExportCriteriaModel criteriaModel) throws IOException;
    ExportType getType();
}
