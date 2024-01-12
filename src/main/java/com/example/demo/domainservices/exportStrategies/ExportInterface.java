package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;

import java.io.IOException;

public interface ExportInterface {
    ExportReturnModel getFile(ExportCriteriaModel criteriaModel) throws IOException;
    ExportType getType();
}
