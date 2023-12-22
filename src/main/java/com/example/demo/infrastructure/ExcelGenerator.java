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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {
    private final SXSSFWorkbook workbook;
    private Map<String,SXSSFSheet> sheets = new HashMap<>();

    public ExcelGenerator() {
        workbook = new SXSSFWorkbook();
    }
    private void writeHeader(SXSSFSheet sheet) {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short)16);
        style.setFont(font);
        createCell(row, 0, "Слово", style, sheet);
        createCell(row, 1, "Перевод", style, sheet);
        createCell(row, 2, "Дата добавления", style, sheet);
        createCell(row, 3, "Кем добавлено (ФИО)", style, sheet);
        createCell(row, 4, "Кем добавлено (Логин)", style, sheet);
        createCell(row, 5, "Кем добавлено (Роль)", style, sheet);
    }
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style, SXSSFSheet sheet) {
        //sheet.autoSizeColumn(columnCount,true);
        Cell cell = row.createCell(columnCount);

        if(valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        }

        if(valueOfCell instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) valueOfCell);
        }

        cell.setCellStyle(style);
    }
    private void write(SXSSFSheet sheet, ExcelModel model) {
        int rowCount = sheet.getLastRowNum()+1;
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short)14);
        style.setFont(font);
        Row row = sheet.createRow(rowCount);

        createCell(row, 0, model.getWord(), style, sheet);
        createCell(row, 1, model.getTranslate(), style, sheet);
        createCell(row, 2, model.getCreatedAt(), style, sheet);
        createCell(row, 3, model.getCreatedByFullName(), style, sheet);
        createCell(row, 4, model.getCreatedByLogin(), style, sheet);
        createCell(row, 5, model.getCreatedByRole(), style, sheet);
    }
    public void addData(List < ExcelModel > modelList){
        SXSSFSheet sheet;

        for(ExcelModel model: modelList) {
            var dictName = model.getWordLanguage().concat(" - ").concat(model.getTranslateLanguage());
            sheet = sheets.get(dictName);
            if(sheet==null) {
                sheet = workbook.createSheet(dictName);
                writeHeader(sheet);
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
