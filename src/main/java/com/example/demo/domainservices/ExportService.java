package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domainservices.exportStrategies.ExportInterface;
import com.example.demo.infrastructure.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportService {
    private final String NO_SUCH_JOB_ERROR_CODE = "NO_SUCH_JOB_ERROR_CODE";
    private final String NO_SUCH_JOB_ERROR_MESSAGE = "No such job was found";
    private final String NOT_EXPORT_ERROR_CODE = "NOT_EXPORT_ERROR_CODE";
    private final String NOT_EXPORT_ERROR_MESSAGE = "It is not an export job";
    private final String NOT_READY_ERROR_CODE = "NOT_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE = "Your file is not ready yet, it's status is: ";
    private final String FAILED_READ_PARAMS_ERROR_CODE = "FAILED_READ_PARAMS_ERROR_CODE";
    private static final String FAILED_READ_PARAMS_ERROR_MESSAGE = "Failed to read parameters";

    @Autowired
    private JobService jobService;


    public ExportReturnModel getFile(UUID taskId) {
        var job = jobService.findById(taskId);
        if (job == null) {
            return new ExportReturnModel(NO_SUCH_JOB_ERROR_CODE, NO_SUCH_JOB_ERROR_MESSAGE);
        }
        if (job.getStatus() != TaskStatus.SUCCESS) {
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE + job.getStatus());
        }
        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString()));
        } catch (Exception e) {
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }

        var paramsOptional = JsonUtils.fromJson(job.getParams(), ExportCriteriaModel.class);
        if (paramsOptional.isEmpty()) {
            return new ExportReturnModel(FAILED_READ_PARAMS_ERROR_CODE, FAILED_READ_PARAMS_ERROR_MESSAGE);
        } else {
            return new ExportReturnModel(taskId.toString(), paramsOptional.get().getFileExtension(), file);
        }

    }

    public GuidResultModel export(ExportCriteriaModel criteriaModel) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(criteriaModel.getExportType().getJobType());
        jobModel.setParams(JsonUtils.toJson(criteriaModel));
        return jobService.addNew(jobModel);
    }
}
