package com.example.demo.domainservices.exportStrategies;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.List;

public class ExportUtils {
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
