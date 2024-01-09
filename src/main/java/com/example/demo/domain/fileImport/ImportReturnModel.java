package com.example.demo.domain.fileImport;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportReturnModel extends GeneralResultModel {
    String fileName;
    String fileExtension;
    byte[] fileBody;
    public ImportReturnModel(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
