package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.association.AssociationCriteriaModel;
import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.export.AssociationsExportModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domain.word.WordModelReturnEnriched;
import com.example.demo.domainservices.AssociationService;
import com.example.demo.domainservices.ExportInterface;
import com.example.demo.domainservices.UserService;
import com.example.demo.domainservices.WordService;
import com.example.demo.infrastructure.ExcelUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AssociationsExportImpl implements ExportInterface {

    private static final String FILE_IS_EMPTY_ERROR_CODE = "FILE_IS_EMPTY_ERROR_CODE";
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String NO_USER_PASSED_FILTER_ERROR_CODE = "NO_USER_PASSED_FILTER";
    private static final String NO_USER_PASSED_FILTER_ERROR_MESSAGE = "Ни один пользователь не прошёл условия фильтра";
    private final String TOO_MANY_USERS_FILTERED_ERROR_CODE = "TOO_MANY_USERS_FILTERED_ERROR_CODE";
    private final String TOO_MANY_USERS_FILTERED_ERROR_MESSAGE = "Слишком много людей было найдено в фильтре";

    private static final String FILE_NAME="AssociationsExport";
    private static final String FILE_EXTENSION=".xlsx";
    private final List<String> HEADERS = List.of(
            "Слово",
            "Перевод",
            "Дата добавления",
            "Кем добавлено (ФИО)",
            "Кем добавлено (Логин)",
            "Кем добавлено (Роль)"
    );
    DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    @Inject
    private UserService userService;
    @Inject
    private WordService wordService;
    @Inject
    private AssociationService associationService;

    @Override
    public ExportReturnModel getFile(ExportCriteriaModel criteriaModel) throws IOException {
        var workbook = new SXSSFWorkbook();
        var sheets = new HashMap<String, SXSSFSheet>();
        var defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        var boldStyle = ExcelUtils.getBoldStyle(workbook);
        PageResult<AssociationsExportModel> pageResult;
        criteriaModel.setSize(500);
        criteriaModel.setPageNumber(1);
        do {
            pageResult = getAssociationsExportModels(criteriaModel);
            if (pageResult.getErrorCode() != null) {
                return new ExportReturnModel(pageResult.getErrorCode(), pageResult.getErrorMessage());
            }
            if (pageResult.getTotalCount() == 0) {
                return new ExportReturnModel(FILE_IS_EMPTY_ERROR_CODE, FILE_IS_EMPTY_ERROR_MESSAGE);
            }
            addData(pageResult.getPageContent(), workbook, defaultStyle, boldStyle, sheets);
            criteriaModel.setPageNumber(criteriaModel.getPageNumber() + 1);
        }
        while (pageResult.getPageContent().size() == criteriaModel.getSize());
        sheets.forEach((key, sheet) -> {
                    CellRangeAddress region = new CellRangeAddress(0, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
                    RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
                    RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
                }
        );
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            workbook.close();
            return new ExportReturnModel(String.join("_", FILE_NAME ,LocalDateTime.now().format(fileNameFormatter)), FILE_EXTENSION, baos.toByteArray());
        }
    }

    @Override
    public ExportType getType() {
        return ExportType.ASSOCIATIONS_EXPORT;
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

        if(associationPage.getPageContent().isEmpty()){
            return null;
        }

        Set<UUID> wordIdsList = new HashSet<>();
        for (var associationModelReturn : associationPage.getPageContent()) {
            wordIdsList.add(associationModelReturn.getWord());
            wordIdsList.add(associationModelReturn.getTranslation());
        }


        var wordEnrichedList = wordService.getListEnrichedByIds(wordIdsList);
        if(wordEnrichedList.isEmpty()){
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

    private void writeHeader(SXSSFSheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);

        for (int i = 0; i < HEADERS.size(); i++) {
            createCell(row, i, HEADERS.get(i), style);
        }
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, HEADERS.size() - 1);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
    }

    private void createCell(Row row, int columnCount, String valueOfCell, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(valueOfCell);
        cell.setCellStyle(style);
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
                writeHeader(sheet, boldStyle);
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
