package com.example.demo.infrastructure;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.awt.*;

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
}
