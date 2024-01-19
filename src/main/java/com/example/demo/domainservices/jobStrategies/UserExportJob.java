package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.progress.ExportProgress;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.UserService;
import com.example.demo.domainservices.jobStrategies.ExportWriters.AssociationsExportExcelWriter;
import com.example.demo.domainservices.jobStrategies.ExportWriters.UserExportExcelWriter;
import com.example.demo.domainservices.jobStrategies.ExportWriters.WriterInterface;
import com.example.demo.infrastructure.ExcelUtils;
import com.example.demo.infrastructure.JsonUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.infrastructure.ExcelUtils.createCell;
import static com.example.demo.infrastructure.ExcelUtils.writeHeader;

@Log4j2
@Component
public class UserExportJob extends BaseJob {
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String FAILED_READ_PARAMS_EXCEPTION_MESSAGE = "Failed to read parameters";

    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("UserExportJob started");
        ExportCriteriaModel criteriaModel = JsonUtils.fromJson(job.getParams(), ExportCriteriaModel.class)
                .orElseThrow(() -> new CriticalErrorException(FAILED_READ_PARAMS_EXCEPTION_MESSAGE));
        var progress = JsonUtils.fromJson(job.getProgress(), ExportProgress.class)
                .orElse(new ExportProgress(0, 500, 0));

        WriterInterface writer;
        switch (criteriaModel.getFileExtension()) {
            case ".xlsx":
                writer = new UserExportExcelWriter();
                break;
            default:
                throw new CriticalErrorException("Unknown file extension");
        }
        PageResult<UserModelReturn> pageResult;
        criteriaModel.setSize(progress.getPageSize());
        criteriaModel.setPageNumber(progress.getLastPage());
        do {
            progress.setLastPage(progress.getLastPage() + 1);
            criteriaModel.setPageNumber(progress.getLastPage());
            pageResult = getUserExportModels(criteriaModel);
            if (pageResult.getTotalCount() == 0) {
                throw new CriticalErrorException(FILE_IS_EMPTY_ERROR_MESSAGE);
            }

            progressMessageModel.setAllCount(pageResult.getTotalCount());
            progress.setAllCount(pageResult.getTotalCount());

            writer.addData(pageResult.getPageContent());

            progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + pageResult.getPageContent().size());

            job.setProgress(JsonUtils.toJson(progress));
            jobService.update(job);
        } while (pageResult.getPageContent().size() == criteriaModel.getSize());
        if (criteriaModel.getFileExtension() == "xlsx") {
            ((UserExportExcelWriter)writer).doBorders();
        }

        try {
            FileOutputStream fos = new FileOutputStream(job.getJobId().toString());
            writer.write(fos);
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
        log.info("UserExportJob finished");
    }

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    @Override
    public TaskType getType() {
        return TaskType.USER_EXPORT;
    }

    private PageResult<UserModelReturn> getUserExportModels(ExportCriteriaModel criteriaModel) {
        var userCriteriaModel = new UserCriteriaModel();
        userCriteriaModel.setPageNumber(criteriaModel.getPageNumber());
        userCriteriaModel.setSize(criteriaModel.getSize());
        if (StringUtils.isNotBlank(criteriaModel.getByRoleFilter())) {
            userCriteriaModel.setRoleFilter(criteriaModel.getByRoleFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getByLoginFilter())) {
            userCriteriaModel.setLoginFilter(criteriaModel.getByLoginFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getByFullNameFilter())) {
            userCriteriaModel.setFullNameFilter(criteriaModel.getByFullNameFilter());
        }
        return userService.getPage(userCriteriaModel);
    }



}
