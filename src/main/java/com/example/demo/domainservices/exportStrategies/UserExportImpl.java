package com.example.demo.domainservices.exportStrategies;

import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domainservices.ExportInterface;
import com.example.demo.domainservices.UserService;
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserExportImpl implements ExportInterface {
    private static final String FILE_IS_EMPTY_ERROR_CODE = "FILE_IS_EMPTY_ERROR_CODE";
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String FILE_NAME = "UserExport";
    private static final String FILE_EXTENSION = ".xlsx";
    private final List<String> HEADERS = List.of(
            "Логин",
            "ФИО",
            "Роль",
            "Дата добавления"
    );
    DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    @Inject
    private UserService userService;

    @Override
    public ExportReturnModel getFile(ExportCriteriaModel criteriaModel) throws IOException {
        var workbook = new SXSSFWorkbook();
        var sheets = new HashMap<String, SXSSFSheet>();
        var defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        var boldStyle = ExcelUtils.getBoldStyle(workbook);
        PageResult<UserModelReturn> pageResult;
        criteriaModel.setSize(500);
        criteriaModel.setPageNumber(1);
        do {
            pageResult = getUserExportModels(criteriaModel);
            if (pageResult.getTotalCount() == 0) {
                return new ExportReturnModel(FILE_IS_EMPTY_ERROR_CODE, FILE_IS_EMPTY_ERROR_MESSAGE);
            }
            addData(pageResult.getPageContent(), workbook, defaultStyle, boldStyle, sheets);
            criteriaModel.setPageNumber(criteriaModel.getPageNumber() + 1);
        } while (pageResult.getPageContent().size() == criteriaModel.getSize());
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
            return new ExportReturnModel(String.join("_", FILE_NAME, LocalDateTime.now().format(fileNameFormatter)),
                    FILE_EXTENSION,
                    baos.toByteArray());
        }
    }

    @Override
    public ExportType getType() {
        return ExportType.USER_EXPORT;
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
