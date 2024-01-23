package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.params.ImportParams;
import com.example.demo.infrastructure.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class ImportService {
    private final String NO_FILE_EXTENSION_ERROR_CODE = "NO_FILE_EXTENSION_ERROR_CODE";
    private final String NO_FILE_EXTENSION_ERROR_MESSAGE = "Нет расширения файла";
    private final String NO_SUCH_JOB_ERROR_CODE = "NO_SUCH_JOB_ERROR_CODE";
    private final String NO_SUCH_JOB_ERROR_MESSAGE = "No such job was found";
    private final String FILE_IS_EMPTY_ERROR_CODE = "FILE_IS_EMPTY_ERROR_CODE";
    private final String FILE_IS_EMPTY_ERROR_MESSAGE = "Result file is empty";
    private final String NOT_READY_ERROR_CODE = "NOT_READY_ERROR_CODE";
    private final String NOT_READY_ERROR_MESSAGE = "Your file is not ready yet";

    private static final String INPUT_SUFFIX = "_input";
    private static final String OUTPUT_SUFFIX = "_output";

    @Autowired
    private JobService jobService;


    public ImportReturnModel getFile(UUID taskId) {
        var job = jobService.findById(taskId);
        if (job == null) {
            return new ImportReturnModel(NO_SUCH_JOB_ERROR_CODE, NO_SUCH_JOB_ERROR_MESSAGE);
        }
        if (job.getStatus() != TaskStatus.SUCCESS) {
            return new ImportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }
        var progressMessage = JsonUtils.readJSON(job.getProgressMessage(), ProgressMessageModel.class);
        if (progressMessage.isPresent()) {
            if (progressMessage.get().getErrorCount() == 0) {
                return new ImportReturnModel(FILE_IS_EMPTY_ERROR_CODE, FILE_IS_EMPTY_ERROR_MESSAGE);
            }
        }

        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString() + OUTPUT_SUFFIX));
        } catch (Exception e) {
            return new ImportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }

        var params = JsonUtils.readJSON(job.getParams(), ImportParams.class);
        return params.map(importParams ->
                        new ImportReturnModel(taskId.toString(), importParams.getFileExtension(), file))
                .orElseGet(() ->
                        new ImportReturnModel(NO_FILE_EXTENSION_ERROR_CODE, NO_FILE_EXTENSION_ERROR_MESSAGE));
    }

    public GuidResultModel importFile(MultipartFile file, ImportType type) {
        var jobId = UUID.randomUUID();
        try (FileOutputStream fos = new FileOutputStream(jobId + INPUT_SUFFIX)) {
            fos.write(file.getBytes());
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
        var jobModel = new JobModelPost();
        jobModel.setTaskType(type.getJobType());

        return jobService.addNew(jobModel, jobId);
    }
}
