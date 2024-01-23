package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class AssociationsExportImpl implements ExportInterface {

    private final String NOT_READY_ERROR_CODE="NOR_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE="Your file is not ready yet";
    private static final String FILE_EXTENSION=".xlsx";

    @Autowired
    JobService jobService;

    @Override
    public GuidResultModel export(ExportCriteriaModel criteriaModel) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(TaskType.ASSOCIATIONS_EXPORT);
        jobModel.setParams(JsonUtils.toString(criteriaModel));
        return jobService.addNew(jobModel);
    }

    @Override
    public ExportReturnModel getFile(UUID taskId) {
        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString()));
        }
        catch (Exception e){
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }
        return new ExportReturnModel(taskId.toString(),FILE_EXTENSION, file);
    }

    @Override
    public ExportType getType() {
        return ExportType.ASSOCIATIONS_EXPORT;
    }


}
