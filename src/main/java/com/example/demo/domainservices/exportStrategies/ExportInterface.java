package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;

import java.io.IOException;
import java.util.UUID;

public interface ExportInterface {
    GuidResultModel export(ExportCriteriaModel criteriaModel);
    ExportReturnModel getFile(UUID taskId);
    ExportType getType();
}
