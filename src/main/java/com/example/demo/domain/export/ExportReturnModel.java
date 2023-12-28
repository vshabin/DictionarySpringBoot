package com.example.demo.domain.export;

import com.example.demo.domain.common.GeneralResultModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportReturnModel extends GeneralResultModel {
    String fileName;
    String fileExtension;
    byte[] fileBody;
    public ExportReturnModel(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
