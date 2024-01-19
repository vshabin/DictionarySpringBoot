package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.fileImport.ImportReturnModel;
import com.example.demo.domain.fileImport.ImportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.ImportParams;
import com.example.demo.domainservices.exportStrategies.ExportInterface;
import com.example.demo.domainservices.importStrategies.ImportInterface;
import com.example.demo.infrastructure.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class ImportService {
    private final String NO_FILE_EXTENSION_ERROR_CODE = "NO_FILE_EXTENSION_ERROR_CODE";
    private final String NO_FILE_EXTENSION_ERROR_MESSAGE = "Нет расширения файла";
    private final String NO_SUCH_JOB_ERROR_CODE = "NO_SUCH_JOB_ERROR_CODE";
    private final String NO_SUCH_JOB_ERROR_MESSAGE = "No such job was found";
    private final String NOT_IMPORT_ERROR_CODE = "NOT_EXPORT_ERROR_CODE";
    private final String NOT_IMPORT_ERROR_MESSAGE = "It is not an export job";
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

        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString()+OUTPUT_SUFFIX));
        }
        catch (Exception e){
            return new ImportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }

        var params = JsonUtils.fromJson(job.getParams(), ImportParams.class);
        if(params.isEmpty()){
            return new ImportReturnModel(NO_FILE_EXTENSION_ERROR_CODE, NO_FILE_EXTENSION_ERROR_MESSAGE);
        }
        return new ImportReturnModel(taskId.toString(),params.get().getFileExtension() , file);
    }

    public GuidResultModel importFile(MultipartFile file, ImportType type) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(type.getJobType());
        var jobId = jobService.addNew(jobModel);
        try (FileOutputStream fos = new FileOutputStream(jobId.getId().toString()+INPUT_SUFFIX)){
            fos.write(file.getBytes());
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
        return jobId;
    }
}
