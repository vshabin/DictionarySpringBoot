package com.example.demo.domainservices.importStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ImportInterface {
    ImportType getType();

    GuidResultModel importFile(MultipartFile file);

    ImportReturnModel getFile(UUID taskId);
}
