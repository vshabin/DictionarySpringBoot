package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.association.AssociationCriteriaModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.export.AssociationsExportModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.progress.ExportProgress;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.domainservices.AssociationService;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.UserService;
import com.example.demo.domainservices.WordService;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.domainservices.exportStrategies.ExportUtils.createCell;
import static com.example.demo.domainservices.exportStrategies.ExportUtils.writeHeader;

@Log4j2
@Component
public class AssociationsExportJob extends BaseJob {
    private static final String FAILED_READ_PARAMS_EXCEPTION_MESSAGE = "Failed to read parameters";
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String FILE_IS_EMPTY_ERROR_CODE = "FILE_IS_EMPTY_ERROR_CODE";
    private static final String NO_USER_PASSED_FILTER_ERROR_CODE = "NO_USER_PASSED_FILTER";
    private static final String NO_USER_PASSED_FILTER_ERROR_MESSAGE = "Ни один пользователь не прошёл условия фильтра";
    private final String TOO_MANY_USERS_FILTERED_ERROR_CODE = "TOO_MANY_USERS_FILTERED_ERROR_CODE";
    private final String TOO_MANY_USERS_FILTERED_ERROR_MESSAGE = "Слишком много людей было найдено в фильтре";

    private static final String FILE_NAME = "AssociationsExport";
    private static final String FILE_EXTENSION = ".xlsx";
    private final List<String> HEADERS = List.of(
            "Слово",
            "Перевод",
            "Дата добавления",
            "Кем добавлено (ФИО)",
            "Кем добавлено (Логин)",
            "Кем добавлено (Роль)"
    );
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");

    @Autowired
    private UserService userService;
    @Autowired
    private WordService wordService;
    @Autowired
    private AssociationService associationService;
    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    public int getMaxAttempt() {
        return 3;
    }

    @Override
    public void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("Start exporting associations");
        ExportCriteriaModel criteriaModel = JsonUtils.fromJson(job.getParams(), ExportCriteriaModel.class)
                .orElseThrow(() -> new CriticalErrorException(FAILED_READ_PARAMS_EXCEPTION_MESSAGE));
        var progress = JsonUtils.fromJson(job.getProgress(), ExportProgress.class)
                .orElse(new ExportProgress(0, 500, 0));
        var workbook = new SXSSFWorkbook();
        var sheets = new HashMap<String, SXSSFSheet>();
        var defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        var boldStyle = ExcelUtils.getBoldStyle(workbook);
        PageResult<AssociationsExportModel> pageResult;
        criteriaModel.setSize(progress.getPageSize());
        criteriaModel.setPageNumber(progress.getLastPage());
        do {
            progress.setLastPage(progress.getLastPage() + 1);
            criteriaModel.setPageNumber(progress.getLastPage());
            pageResult = getAssociationsExportModels(criteriaModel);
            if (pageResult == null) {
                break;
            }
            if (pageResult.getTotalCount() == 0) {
                throw new CriticalErrorException(FILE_IS_EMPTY_ERROR_MESSAGE);
            }
            if (pageResult.getErrorCode() != null) {
                throw new CriticalErrorException(pageResult.getErrorMessage());
            }
            progressMessageModel.setAllCount(pageResult.getTotalCount());
            progress.setAllCount(pageResult.getTotalCount());

            addData(pageResult.getPageContent(), workbook, defaultStyle, boldStyle, sheets);

            progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + pageResult.getPageContent().size());

            job.setProgress(JsonUtils.toJson(progress));
            jobService.update(job);
        }
        while (pageResult.getPageContent().size() == criteriaModel.getSize());
        sheets.forEach((key, sheet) -> {
                    CellRangeAddress region = new CellRangeAddress(sheet.getLastRowNum() - 1, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
                    RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
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
    public TaskType getType() {
        return TaskType.ASSOCIATIONS_EXPORT;
    }

    private PageResult<AssociationsExportModel> getAssociationsExportModels(ExportCriteriaModel criteriaModel) {

        var userCriteriaModel = new UserCriteriaModel();
        if (StringUtils.isNotBlank(criteriaModel.getByRoleFilter())) {
            userCriteriaModel.setRoleFilter(criteriaModel.getByRoleFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getByLoginFilter())) {
            userCriteriaModel.setLoginFilter(criteriaModel.getByLoginFilter());
        }
        if (StringUtils.isNotBlank(criteriaModel.getByFullNameFilter())) {
            userCriteriaModel.setFullNameFilter(criteriaModel.getByFullNameFilter());
        }
        var usersList = userService.getFilteredList(userCriteriaModel);

        if (usersList.size() > 1000) {
            return new PageResult<>(TOO_MANY_USERS_FILTERED_ERROR_CODE, TOO_MANY_USERS_FILTERED_ERROR_MESSAGE);
        }
        if (usersList.isEmpty()) {
            return new PageResult<>(NO_USER_PASSED_FILTER_ERROR_CODE, NO_USER_PASSED_FILTER_ERROR_MESSAGE);
        }
        var usersMap = usersList.stream()
                .collect(Collectors.toMap(UserModelReturn::getId, Function.identity()));

        var associationCriteriaModel = new AssociationCriteriaModel();
        associationCriteriaModel.setPageNumber(criteriaModel.getPageNumber());
        associationCriteriaModel.setSize(criteriaModel.getSize());
        if (criteriaModel.getFromFilter() != null) {
            associationCriteriaModel.setFromFilter(criteriaModel.getFromFilter());
        }
        if (criteriaModel.getToFilter() != null) {
            associationCriteriaModel.setToFilter(criteriaModel.getToFilter());
        }

        associationCriteriaModel.setCreatedByUUID(usersMap.keySet());

        var associationPage = associationService.getPage(associationCriteriaModel);

        if (associationPage.getPageContent().isEmpty()) {
            return null;
        }

        Set<UUID> wordIdsList = new HashSet<>();
        for (var associationModelReturn : associationPage.getPageContent()) {
            wordIdsList.add(associationModelReturn.getWord());
            wordIdsList.add(associationModelReturn.getTranslation());
        }


        var wordEnrichedList = wordService.getListEnrichedByIds(wordIdsList);
        if (wordEnrichedList.isEmpty()) {
            return null;
        }
        var wordMap = wordEnrichedList.stream()
                .collect(Collectors.toMap(WordModelReturnEnriched::getId, Function.identity()));

        List<AssociationsExportModel> pageContent = new ArrayList<>();
        associationPage.getPageContent().forEach(association ->
                pageContent.add(new AssociationsExportModel(
                        wordMap.get(association.getWord()),
                        wordMap.get(association.getTranslation()),
                        association.getCreatedAt(),
                        usersMap.get(association.getCreatedByUserId())
                ))
        );
        return new PageResult<>(pageContent, associationPage.getTotalCount());
    }


    private void write(SXSSFSheet sheet, AssociationsExportModel model, CellStyle style) {
        int rowCount = sheet.getLastRowNum() + 1;

        Row row = sheet.createRow(rowCount);
        var cellNum = 0;
        createCell(row, cellNum++, model.getWord() != null ? model.getWord().getWord() : null, style);
        createCell(row, cellNum++, model.getTranslation() != null ? model.getTranslation().getWord() : null, style);
        createCell(row, cellNum++, model.getCreatedAt() != null ? model.getCreatedAt().format(formatter) : null, style);
        createCell(row, cellNum++, model.getUser() != null ? model.getUser().getFullName() : null, style);
        createCell(row, cellNum++, model.getUser().getLogin(), style);
        createCell(row, cellNum++, model.getUser() != null ? model.getUser().getRole().name() : null, style);
        CellRangeAddress region = new CellRangeAddress(sheet.getLastRowNum() - 1, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
        RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
    }

    private void addData(List<AssociationsExportModel> modelList,
                         SXSSFWorkbook workbook,
                         CellStyle defaultStyle,
                         CellStyle boldStyle,
                         Map<String, SXSSFSheet> sheets) {
        SXSSFSheet sheet;

        for (AssociationsExportModel model : modelList) {
            var dictName = String.join(" - ", model.getWord().getLanguageName(), model.getTranslation().getLanguageName());
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
