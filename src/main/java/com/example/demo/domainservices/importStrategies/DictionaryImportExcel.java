package com.example.demo.domainservices.importStrategies;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.JobService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Component
public class DictionaryImportExcel implements ImportInterface {
    private static final String FILE_EXTENSION = ".xlsx";
    private static final String INPUT_SUFFIX = "_input";
    private static final String OUTPUT_SUFFIX = "_output";
    private final String NOT_READY_ERROR_CODE = "NOR_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE = "Your file is not ready yet";

    @Autowired
    JobService jobService;

    @Override
    public ImportType getType() {
        return ImportType.DICTIONARY_IMPORT;
    }

    @Override
    public GuidResultModel importFile(MultipartFile file) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(TaskType.DICTIONARY_IMPORT_EXCEL);
        var jobId = jobService.addNew(jobModel);
        try (FileOutputStream fos = new FileOutputStream(jobId.getId().toString() + INPUT_SUFFIX)) {
            fos.write(file.getBytes());
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
        return jobId;
    }

    @Override
    public ImportReturnModel getFile(UUID taskId) {
        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString() + OUTPUT_SUFFIX));
        } catch (Exception e) {
            return new ImportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }
        return new ImportReturnModel(taskId.toString(), FILE_EXTENSION, file);
    }

}
