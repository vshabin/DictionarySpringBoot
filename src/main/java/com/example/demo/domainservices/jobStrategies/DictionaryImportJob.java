package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.association.AssociationModelAdd;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.ImportParams;
import com.example.demo.domain.job.progress.ImportProgress;
import com.example.demo.domain.word.WordModelPost;
import com.example.demo.domainservices.AssociationService;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.LanguageService;
import com.example.demo.domainservices.WordService;
import com.example.demo.infrastructure.ExcelUtils;
import com.example.demo.infrastructure.JsonUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import static com.example.demo.infrastructure.ExcelUtils.createCell;
import static com.example.demo.infrastructure.ExcelUtils.writeHeader;

@Log4j2
@Component
public class DictionaryImportJob extends BaseJob {
    private static final String EMPTY_FIELD_ERROR = "Поле не должно быть пустым";
    private static final String LANGUAGE_DOESNT_EXIST_ERROR = "Такого языка не существует";
    private static final String WRONG_WORD_ERROR = "Слово не соответсвует правилу языка";
    private static final String FILE_READ_ERROR = "Не удалось прочитать загруженный файл";

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
    private static final String INPUT_SUFFIX = "_input";
    private static final String OUTPUT_SUFFIX = "_output";
    private static final String FILE_EXTENSION = ".xlsx";

    @Inject
    private LanguageService languageService;
    @Inject
    private WordService wordService;
    @Inject
    private AssociationService associationService;
    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("Starting DictionaryImport");

        var params = new ImportParams();
        params.setFileExtension(FILE_EXTENSION);
        job.setParams(JsonUtils.toString(params));

        File file;
        try {
            file = new File(job.getJobId().toString() + INPUT_SUFFIX);
            try (XSSFWorkbook inputWorkbook = new XSSFWorkbook(file);
                 SXSSFWorkbook outputWorkbook = new SXSSFWorkbook()) {

                var progress = JsonUtils.readJSON(job.getProgress(), ImportProgress.class)
                        .orElse(new ImportProgress(0, 0));

                var inputSheet = inputWorkbook.getSheetAt(0);

                var outputSheet = outputWorkbook.createSheet();

                var defaultStyle = ExcelUtils.getDefaultStyle(outputWorkbook);
                var boldStyle = ExcelUtils.getBoldStyle(outputWorkbook);
                var errorStyle = ExcelUtils.getErrorStyle(outputWorkbook);

                writeHeader(outputSheet, boldStyle, OUTPUT_HEADERS);

                progress.setAttAll(inputSheet.getLastRowNum());
                progressMessageModel.setAllCount(inputSheet.getLastRowNum() + 1);

                for (int rowNum = progress.getLastRow(); rowNum <= inputSheet.getLastRowNum(); rowNum++) {
                    progress.setLastRow(rowNum);
                    var row = inputSheet.getRow(rowNum);
                    boolean thereIsError = false;
                    for (int i = 0; i < INPUT_HEADERS.size(); i++) {
                        if (row.getCell(i) == null) {
                            writeError(outputSheet, row, defaultStyle, errorStyle, i, EMPTY_FIELD_ERROR, job, progressMessageModel);
                            thereIsError = true;
                            break;
                        }
                        row.getCell(i).setCellType(CellType.STRING);
                        if (StringUtils.isBlank(row.getCell(i).getStringCellValue())) {
                            writeError(outputSheet, row, defaultStyle, errorStyle, i, EMPTY_FIELD_ERROR, job, progressMessageModel);
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
                        writeError(outputSheet, row, defaultStyle, errorStyle, 1, LANGUAGE_DOESNT_EXIST_ERROR, job, progressMessageModel);
                        continue;
                    }
                    if (translationLanguageModel == null) {
                        writeError(outputSheet, row, defaultStyle, errorStyle, 3, LANGUAGE_DOESNT_EXIST_ERROR, job, progressMessageModel);
                        continue;
                    }
                    var word = row.getCell(0).getStringCellValue().toLowerCase().trim();
                    var translation = row.getCell(2).getStringCellValue().toLowerCase().trim();

                    if (!word.matches(wordLanguageModel.getRegEx())) {
                        writeError(outputSheet, row, defaultStyle, errorStyle, 0, WRONG_WORD_ERROR, job, progressMessageModel);
                        continue;
                    }

                    if (!translation.matches(translationLanguageModel.getRegEx())) {
                        writeError(outputSheet, row, defaultStyle, errorStyle, 2, WRONG_WORD_ERROR, job, progressMessageModel);
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
                        writeError(outputSheet, row, defaultStyle, errorStyle, 0, result.getErrorMessage(), job, progressMessageModel);
                        continue;
                    }
                    progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + 1);
                    job.setProgress(JsonUtils.toString(progressMessageModel));
                    jobService.update(job);
                }

                try {
                    FileOutputStream fos = new FileOutputStream(job.getJobId().toString() + OUTPUT_SUFFIX);
                    outputWorkbook.write(fos);
                } catch (Exception e) {
                    throw new CriticalErrorException(e.getMessage());
                }
            } catch (Exception e) {
                throw new CriticalErrorException(FILE_READ_ERROR);
            }
        } catch (Exception e) {
            throw new CriticalErrorException(FILE_READ_ERROR);
        }

        log.info("Finished DictionaryImport");
    }

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    @Override
    public TaskType getType() {
        return TaskType.DICTIONARY_IMPORT_EXCEL;
    }

    private void writeError(SXSSFSheet sheet, Row inputRow, CellStyle defaultStyle, CellStyle errorStyle, int errorPosition, String errorMessage, JobModelReturn job, ProgressMessageModel progressMessageModel) {
        int rowCount = sheet.getLastRowNum() + 1;
        progressMessageModel.setErrorCount(progressMessageModel.getErrorCount() + 1);
        job.setProgress(JsonUtils.toString(progressMessageModel));
        jobService.update(job);
        Row row = sheet.createRow(rowCount);
        for (int cellNum = 0; cellNum < OUTPUT_HEADERS.size() - 1; cellNum++) {
            createCell(row, cellNum, inputRow.getCell(cellNum) == null ? null : inputRow.getCell(cellNum).getStringCellValue(), errorPosition == cellNum ? errorStyle : defaultStyle);
        }
        createCell(row, OUTPUT_HEADERS.size() - 1, errorMessage, defaultStyle);
    }
}
