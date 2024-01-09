package com.example.demo.domainservices;

import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportInterface {
    ImportReturnModel readFile(MultipartFile file) throws IOException;
    ImportType getType();
}
