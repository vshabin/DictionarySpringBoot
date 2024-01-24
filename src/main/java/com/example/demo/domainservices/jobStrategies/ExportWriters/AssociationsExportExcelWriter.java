package com.example.demo.domainservices.jobStrategies.ExportWriters;

import com.example.demo.domain.export.AssociationsExportModel;
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

public class AssociationsExportExcelWriter implements AssociationsExportWriterInterface {
    private Workbook workbook;
    private final HashMap<String, Sheet> sheets;
    private final CellStyle defaultStyle;
    private final CellStyle boldStyle;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");

    private final List<String> HEADERS = List.of(
            "Слово",
            "Перевод",
            "Дата добавления",
            "Кем добавлено (ФИО)",
            "Кем добавлено (Логин)",
            "Кем добавлено (Роль)"
    );

    public AssociationsExportExcelWriter(FileInputStream file) {
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
    public void addData(List<AssociationsExportModel> modelList) {
        Sheet sheet;
        for (AssociationsExportModel model : modelList) {
            var dictName = String.join(" - ", model.getWord().getLanguageName(), model.getTranslation().getLanguageName());
            sheet = workbook.getSheet(dictName);
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

    private void writeData(Sheet sheet, AssociationsExportModel model, CellStyle style) {
        int rowCount = sheet.getLastRowNum() + 1;

        Row row = sheet.createRow(rowCount);
        try{
            var cellNum = 0;
            createCell(row, cellNum++, model.getWord() != null ? model.getWord().getWord() : null, style);
            createCell(row, cellNum++, model.getTranslation() != null ? model.getTranslation().getWord() : null, style);
            createCell(row, cellNum++, model.getCreatedAt() != null ? model.getCreatedAt().format(formatter) : null, style);
            //createCell(row, cellNum++, model.getUser() != null ? model.getUser().getFullName() : null, style);
            createCell(row, cellNum++, model.getUser().getFullName() , style);
            createCell(row, cellNum++, model.getUser().getLogin(), style);
            createCell(row, cellNum++, model.getUser() != null ? model.getUser().getRole().name() : null, style);
            CellRangeAddress region = new CellRangeAddress(sheet.getLastRowNum() - 1, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
            RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
            RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
        } catch (Exception e) {
            for(int i = 0; i <HEADERS.size();i++){
                row.removeCell(row.getCell(i));
            }
            sheet.removeRow(row);
            throw e;
        }

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
