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

import static com.example.demo.domainservices.exportStrategies.ExportUtils.createCell;
import static com.example.demo.domainservices.exportStrategies.ExportUtils.writeHeader;

@Log4j2
@Component
public class UserExportJob extends BaseJob {
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String FAILED_READ_PARAMS_EXCEPTION_MESSAGE = "Failed to read parameters";
    private final List<String> HEADERS = List.of(
            "Логин",
            "ФИО",
            "Роль",
            "Дата добавления"
    );
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
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

        var workbook = new SXSSFWorkbook();
        var sheets = new HashMap<String, SXSSFSheet>();
        var defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        var boldStyle = ExcelUtils.getBoldStyle(workbook);
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

            addData(pageResult.getPageContent(), workbook, defaultStyle, boldStyle, sheets);

            progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + pageResult.getPageContent().size());

            job.setProgress(JsonUtils.toJson(progress));
            jobService.update(job);
        } while (pageResult.getPageContent().size() == criteriaModel.getSize());
        sheets.forEach((key, sheet) -> {
                    CellRangeAddress region = new CellRangeAddress(0, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
                    RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
                }
        );

        try {
            FileOutputStream fos = new FileOutputStream(job.getJobId().toString());
            workbook.write(fos);
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
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

    private void write(SXSSFSheet sheet, UserModelReturn model, CellStyle style) {
        int rowCount = sheet.getLastRowNum() + 1;

        Row row = sheet.createRow(rowCount);
        var cellNum = 0;
        createCell(row, cellNum++, model.getLogin(), style);
        createCell(row, cellNum++, model.getFullName(), style);
        createCell(row, cellNum++, model.getRole() != null ? model.getRole().name() : null, style);
        createCell(row, cellNum++, model.getCreatedAt() != null ? model.getCreatedAt().format(formatter) : null, style);
    }

    private void addData(List<UserModelReturn> modelList,
                         SXSSFWorkbook workbook,
                         CellStyle defaultStyle,
                         CellStyle boldStyle,
                         Map<String, SXSSFSheet> sheets) {
        SXSSFSheet sheet;

        for (UserModelReturn model : modelList) {
            var dictName = model.getRole().name();
            sheet = sheets.get(dictName);
            if (sheet == null) {
                sheet = workbook.createSheet(dictName);
                sheet.trackAllColumnsForAutoSizing();
                writeHeader(sheet, boldStyle, HEADERS);
                for (int i = 0; i < HEADERS.size(); i++) {
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1280);
                }
                sheet.untrackAllColumnsForAutoSizing();
                sheets.put(dictName, sheet);
            }
            write(sheet, model, defaultStyle);
        }
    }
}
