package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.UserService;
import com.example.demo.infrastructure.ExcelUtils;
import com.example.demo.infrastructure.JsonUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.demo.domainservices.exportStrategies.ExportUtils.writeHeader;

@Component
public class UserExportImpl implements ExportInterface {
    private final String NOT_READY_ERROR_CODE="NOR_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE="Your file is not ready yet";

    private static final String FILE_EXTENSION = ".xlsx";

    @Autowired
    JobService jobService;

    @Override
    public GuidResultModel export(ExportCriteriaModel criteriaModel) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(TaskType.USER_EXPORT);
        jobModel.setParams(JsonUtils.toJson(criteriaModel));
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
        return ExportType.USER_EXPORT;
    }
}
