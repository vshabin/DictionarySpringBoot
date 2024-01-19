package com.example.demo.domainservices.jobStrategies.ExportWriters;

import com.example.demo.domain.export.AssociationsExportModel;
import com.example.demo.infrastructure.ExcelUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.infrastructure.ExcelUtils.createCell;
import static com.example.demo.infrastructure.ExcelUtils.writeHeader;

public class AssociationsExportExcelWriter implements WriterInterface{
    private final SXSSFWorkbook workbook;
    private final HashMap<String, SXSSFSheet> sheets;
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
    public AssociationsExportExcelWriter() {
        workbook = new SXSSFWorkbook();
        sheets = new HashMap<String, SXSSFSheet>();
        defaultStyle = ExcelUtils.getDefaultStyle(workbook);
        boldStyle = ExcelUtils.getBoldStyle(workbook);
    }

    @Override
    public void addData(List<?> modelList) {
        SXSSFSheet sheet;
        for (AssociationsExportModel model : (List<AssociationsExportModel>)modelList) {
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

    public void doBorders(){
        sheets.forEach((key, sheet) -> {
                    CellRangeAddress region = new CellRangeAddress(sheet.getLastRowNum() - 1, sheet.getLastRowNum(), 0, HEADERS.size() - 1);
                    RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
                }
        );
    }
    public void write(OutputStream stream) throws IOException {
        workbook.write(stream);
    }
}
