package com.example.demo.infrastructure;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.awt.*;
import java.util.List;

public class ExcelUtils {
    public static CellStyle getDefaultStyle(Workbook workbook){
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        return style;
    }
    public static CellStyle getBoldStyle(Workbook workbook){
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static  CellStyle getErrorStyle(Workbook workbook){
        CellStyle style= workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    public static void writeHeader(SXSSFSheet sheet, CellStyle style, List<String> headers) {
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.size(); i++) {
            createCell(row, i, headers.get(i), style);
        }
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, headers.size() - 1);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
    }

    public static void createCell(Row row, int columnCount, String valueOfCell, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(valueOfCell);
        cell.setCellStyle(style);
    }
}
