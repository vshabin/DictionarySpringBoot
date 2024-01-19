package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domainservices.exportStrategies.ExportInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportService {
    private final String NO_SUCH_STRATEGY_ERROR_CODE = "NO_SUCH_STRATEGY_ERROR_CODE";
    private final String NO_SUCH_STRATEGY_ERROR_MESSAGE = "No such strategy";
    private final String NO_SUCH_JOB_ERROR_CODE = "NO_SUCH_JOB_ERROR_CODE";
    private final String NO_SUCH_JOB_ERROR_MESSAGE = "No such job was found";
    private final String NOT_EXPORT_ERROR_CODE = "NOT_EXPORT_ERROR_CODE";
    private final String NOT_EXPORT_ERROR_MESSAGE = "It is not an export job";
    private final String NOT_READY_ERROR_CODE = "NOT_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE = "Your file is not ready yet";
    private final Map<ExportType, ExportInterface> strategies;

    @Autowired
    private JobService jobService;

    public ExportService(Collection<ExportInterface> exportImpls) {
        this.strategies = exportImpls.stream()
                .collect(Collectors.toMap(ExportInterface::getType, Function.identity()));
    }

    public ExportReturnModel getFile(UUID taskId) {
        var job = jobService.findById(taskId);
        if (job == null) {
            return new ExportReturnModel(NO_SUCH_JOB_ERROR_CODE, NO_SUCH_JOB_ERROR_MESSAGE);
        }
        var taskType = job.getTaskType().getType();
        if (!(taskType instanceof ExportType)) {
            return new ExportReturnModel(NOT_EXPORT_ERROR_CODE, NOT_EXPORT_ERROR_MESSAGE);
        }
        var strategy = strategies.get((ExportType) taskType);
        if (strategy == null) {
            return new ExportReturnModel(NOT_EXPORT_ERROR_CODE, NOT_EXPORT_ERROR_MESSAGE);
        }
        if (job.getStatus() != TaskStatus.SUCCESS) {
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }
        return strategy.getFile(taskId);
    }

    public GuidResultModel export(ExportCriteriaModel criteriaModel) {
        var strategy = strategies.get(criteriaModel.getExportType());
        if (strategy == null) {
            return new GuidResultModel(NO_SUCH_STRATEGY_ERROR_CODE, NO_SUCH_STRATEGY_ERROR_MESSAGE);
        }
        return strategy.export(criteriaModel);
    }
}
