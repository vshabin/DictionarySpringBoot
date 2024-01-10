package com.example.demo.domainservices.importStrategies;

import com.example.demo.domain.association.AssociationModelAdd;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domainservices.*;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class DictionaryImportImpl implements ImportInterface {

    private static final String EMPTY_FIELD_ERROR = "Поле не должно быть пустым";
    private static final String LANGUAGE_DOESNT_EXIST_ERROR = "Такого языка не существует";
    private static final String WRONG_WORD_ERROR = "Слово не соответсвует правилу языка";

    DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    private final List<String> INPUT_HEADERS = List.of(
            "Слово",
            "Язык слова",
            "Перевод",
            "Язык перевода"
    );
    private final List<String> OUTPUT_HEADERS = List.of(
            "Слово",
            "Язык слова",
            "Перевод",
            "Язык перевода",
            "Комментарий"
    );

    private static final String FILE_NAME = "Dictionary_import_errors";
    private static final String FILE_EXTENSION = ".xlsx";

    @Inject
    private LanguageService languageService;
    @Inject
    private WordService wordService;
    @Inject
    private AssociationService associationService;

    @Override
    public ImportReturnModel readFile(MultipartFile file) throws IOException {
        var inputWorkbook = new XSSFWorkbook(file.getInputStream());
        var inputSheet = inputWorkbook.getSheetAt(0);
        var outputWorkbook = new SXSSFWorkbook();
        var outputSheet = outputWorkbook.createSheet();

        var defaultStyle = ExcelUtils.getDefaultStyle(outputWorkbook);
        var boldStyle = ExcelUtils.getBoldStyle(outputWorkbook);
        var errorStyle = ExcelUtils.getErrorStyle(outputWorkbook);

        writeHeader(outputSheet, boldStyle);

        for (int rowNum = 0; rowNum <= inputSheet.getLastRowNum(); rowNum++) {
            var row = inputSheet.getRow(rowNum);
            boolean thereIsError = false;
            for (int i = 0; i < INPUT_HEADERS.size(); i++) {
                if (row.getCell(i) == null) {
                    writeError(outputSheet, row, defaultStyle, errorStyle, i, EMPTY_FIELD_ERROR);
                    thereIsError = true;
                    break;
                } else if (StringUtils.isBlank(row.getCell(i).getStringCellValue())) {
                    writeError(outputSheet, row, defaultStyle, errorStyle, i, EMPTY_FIELD_ERROR);
                    thereIsError = true;
                    break;
                }
            }
            if (thereIsError) {
                continue;
            }

            var wordLanguage = row.getCell(1).getStringCellValue().toLowerCase().trim();
            var translationLanguage = row.getCell(3).getStringCellValue().toLowerCase().trim();

            var wordLanguageModel = languageService.getByName(wordLanguage);
            var translationLanguageModel = languageService.getByName(translationLanguage);

            if (wordLanguageModel == null) {
                writeError(outputSheet, row, defaultStyle, errorStyle, 1, LANGUAGE_DOESNT_EXIST_ERROR);
                continue;
            }
            if (translationLanguageModel == null) {
                writeError(outputSheet, row, defaultStyle, errorStyle, 3, LANGUAGE_DOESNT_EXIST_ERROR);
                continue;
            }

            var word = row.getCell(0).getStringCellValue().toLowerCase().trim();
            var translation = row.getCell(2).getStringCellValue().toLowerCase().trim();

            if (!word.matches(wordLanguageModel.getRegEx())) {
                writeError(outputSheet, row, defaultStyle, errorStyle, 0, WRONG_WORD_ERROR);
                continue;
            }

            if (!translation.matches(translationLanguageModel.getRegEx())) {
                writeError(outputSheet, row, defaultStyle, errorStyle, 2, WRONG_WORD_ERROR);
                continue;
            }

            var wordModel = wordService.getByName(word);
            UUID wordId;
            if (wordModel == null) {
                wordId = wordService.save(new WordModelPost(word, wordLanguageModel.getId())).getId();
            } else {
                wordId = wordModel.getId();
            }

            var translationModel = wordService.getByName(translation);
            UUID translationId;
            if (translationModel == null) {
                translationId = wordService.save(new WordModelPost(translation, translationLanguageModel.getId())).getId();
            } else {
                translationId = translationModel.getId();
            }
            var result = associationService.save(new AssociationModelAdd(wordId, translationId));
            if (result.getErrorCode() != null) {
                writeError(outputSheet, row, defaultStyle, errorStyle, 0, result.getErrorMessage());
                continue;
            }
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            outputWorkbook.write(baos);
            outputWorkbook.close();
            return new ImportReturnModel(String.join("_", FILE_NAME, LocalDateTime.now().format(fileNameFormatter)), FILE_EXTENSION, baos.toByteArray());
        }
    }

    @Override
    public ImportType getType() {
        return ImportType.DICTIONARY_IMPORT;
    }

    private void writeError(SXSSFSheet sheet, Row inputRow, CellStyle defaultStyle, CellStyle errorStyle, int errorPosition, String errorMessage) {
        int rowCount = sheet.getLastRowNum() + 1;

        Row row = sheet.createRow(rowCount);
        for (int cellNum = 0; cellNum < OUTPUT_HEADERS.size() - 1; cellNum++) {
            createCell(row, cellNum, inputRow.getCell(cellNum) == null ? null : inputRow.getCell(cellNum).getStringCellValue(), errorPosition == cellNum ? errorStyle : defaultStyle);
        }
        createCell(row, OUTPUT_HEADERS.size() - 1, errorMessage, defaultStyle);
    }

    private void writeHeader(SXSSFSheet sheet, CellStyle style) {
        var row = sheet.createRow(0);

        for (int i = 0; i < OUTPUT_HEADERS.size(); i++) {
            createCell(row, i, OUTPUT_HEADERS.get(i), style);
        }
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, OUTPUT_HEADERS.size() - 1);
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
    }

    private void createCell(Row row, int columnCount, String valueOfCell, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(valueOfCell);
        cell.setCellStyle(style);
    }
}
