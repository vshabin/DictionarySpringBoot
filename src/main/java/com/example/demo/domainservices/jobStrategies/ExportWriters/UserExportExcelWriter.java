package com.example.demo.domainservices.jobStrategies.ExportWriters;

import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.ExcelUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.infrastructure.ExcelUtils.createCell;
import static com.example.demo.infrastructure.ExcelUtils.writeHeader;

public class UserExportExcelWriter implements UserExportWriterInterface {
    private Workbook workbook;
    private final HashMap<String, Sheet> sheets;
    private final CellStyle defaultStyle;
    private final CellStyle boldStyle;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    private final List<String> HEADERS = List.of(
            "Логин",
            "ФИО",
            "Роль",
            "Дата добавления"
    );

    public UserExportExcelWriter(FileInputStream file) {
        try{
            workbook = new XSSFWorkbook(file);
        }
        catch (Exception e) {
            workbook = new SXSSFWorkbook();
        }
        sheets = new HashMap<String, Sheet>();
        defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        boldStyle = ExcelUtils.getBoldStyle(workbook);
    }

    @Override
    public void addData(List<UserModelReturn> modelList) {
        Sheet sheet;
        for (UserModelReturn model : modelList) {
            var dictName = model.getRole().name();
            sheet = sheets.get(dictName);
            if (sheet == null) {
                sheet = workbook.createSheet(dictName);
                if(sheet instanceof SXSSFSheet){
                    ((SXSSFSheet)sheet).trackAllColumnsForAutoSizing();
                    writeHeader(sheet, boldStyle, HEADERS);
                    for (int i = 0; i < HEADERS.size(); i++) {
                        sheet.autoSizeColumn(i);
                        sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1280);
                    }
                    ((SXSSFSheet)sheet).untrackAllColumnsForAutoSizing();
                }
                else {
                    writeHeader(sheet, boldStyle, HEADERS);
                }
                sheets.put(dictName, sheet);
            }
            writeData(sheet, model, defaultStyle);
        }
    }

    private void writeData(Sheet sheet, UserModelReturn model, CellStyle style) {
        int rowCount = sheet.getLastRowNum() + 1;

        Row row = sheet.createRow(rowCount);
        var cellNum = 0;
        createCell(row, cellNum++, model.getLogin(), style);
        createCell(row, cellNum++, model.getFullName(), style);
        createCell(row, cellNum++, model.getRole() != null ? model.getRole().name() : null, style);
        createCell(row, cellNum++, model.getCreatedAt() != null ? model.getCreatedAt().format(formatter) : null, style);
    }


    @Override
    public void write(OutputStream stream) throws IOException {
        workbook.write(stream);
        workbook.close();
    }

    @Override
    public void preWrite() {

    }

    @Override
    public void postWrite() {
        sheets.forEach((key, sheet) -> {
                    CellRangeAddress region = new CellRangeAddress(sheet.getLastRowNum() - 1, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
                    RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
                }
        );
    }
}
