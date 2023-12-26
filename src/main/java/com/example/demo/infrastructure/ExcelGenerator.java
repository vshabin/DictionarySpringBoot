package com.example.demo.infrastructure;

import com.example.demo.domain.association.ExcelModel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {
    private final SXSSFWorkbook workbook;
    private Map<String,SXSSFSheet> sheets = new HashMap<>();
    private CellStyle BOLD_STYLE;
    private CellStyle DEFAULT_STYLE;

    private final List<String> HEADERS= List.of(
            "Слово",
            "Перевод",
            "Дата добавления",
            "Кем добавлено (ФИО)",
            "Кем добавлено (Логин)",
            "Кем добавлено (Роль)"
    );

    public ExcelGenerator() {
        workbook = new SXSSFWorkbook();

        DEFAULT_STYLE= workbook.createCellStyle();
        Font font = workbook.createFont();
        DEFAULT_STYLE.setFont(font);

        BOLD_STYLE = workbook.createCellStyle();
        Font bold_font = workbook.createFont();
        bold_font.setBold(true);
        BOLD_STYLE.setFont(bold_font);

    }
    private void writeHeader(SXSSFSheet sheet) {
        Row row = sheet.createRow(0);

        for(int i=0; i< HEADERS.size();i++){
            createCell(row,i,HEADERS.get(i),BOLD_STYLE,sheet);
        }
    }
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style, SXSSFSheet sheet) {
        Cell cell = row.createCell(columnCount);

        if(valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        }

        if(valueOfCell instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) valueOfCell).toString());
        }
        cell.setCellStyle(style);
    }
    private void write(SXSSFSheet sheet, ExcelModel model) {
        int rowCount = sheet.getLastRowNum()+1;

        Row row = sheet.createRow(rowCount);

        createCell(row, 0, model.getWord(), DEFAULT_STYLE, sheet);
        createCell(row, 1, model.getTranslate(), DEFAULT_STYLE, sheet);
        createCell(row, 2, model.getCreatedAt(), DEFAULT_STYLE, sheet);
        createCell(row, 3, model.getCreatedByFullName(), DEFAULT_STYLE, sheet);
        createCell(row, 4, model.getCreatedByLogin(), DEFAULT_STYLE, sheet);
        createCell(row, 5, model.getCreatedByRole(), DEFAULT_STYLE, sheet);
    }
    public void addData(List <ExcelModel> modelList){
        SXSSFSheet sheet;

        for(ExcelModel model: modelList) {
            var dictName = String.join(" - ", model.getWordLanguage(), model.getTranslateLanguage());
            sheet = sheets.get(dictName);
            if(sheet==null) {
                sheet = workbook.createSheet(dictName);
                sheet.trackAllColumnsForAutoSizing();
                writeHeader(sheet);
                for(int i=0; i< HEADERS.size(); i++) {
                    sheet.autoSizeColumn(i);
                    sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1280);
                }
                sheet.untrackAllColumnsForAutoSizing();
                sheets.put(dictName, sheet);
            }
            write(sheet,model);
        }
    }
    public void generateExcelFile(HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
