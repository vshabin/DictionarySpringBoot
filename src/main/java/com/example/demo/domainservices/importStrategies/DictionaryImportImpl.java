package com.example.demo.domainservices.importStrategies;

import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domainservices.ImportInterface;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class DictionaryImportImpl implements ImportInterface {

    private final List<String> HEADERS = List.of(
            "Слово",
            "Язык слова",
            "Перевод",
            "Язык перевода"
    );
    @Override
    public ImportReturnModel readFile(MultipartFile file) throws IOException {
        var inputWorkbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet inputSheet = inputWorkbook.getSheetAt(0);
        for(int rowNum=0; rowNum<= inputSheet.getLastRowNum(); rowNum++){
            var row = inputSheet.getRow(rowNum);
            for(int colNum=0; colNum<=row.getLastCellNum();colNum++){

            }
        }

        return new ImportReturnModel("aaaa", file.getOriginalFilename());
    }

    @Override
    public ImportType getType() {
        return ImportType.DICTIONARY_IMPORT;
    }
}
