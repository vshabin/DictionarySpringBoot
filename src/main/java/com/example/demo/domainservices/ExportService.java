package com.example.demo.domainservices;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.infrastructure.CommonUtils;
import com.example.demo.infrastructure.JsonUtils;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Log4j2
public class ExportService {
    private static final String NO_SUCH_JOB_ERROR_CODE = "NO_SUCH_JOB_ERROR_CODE";
    private static final String NO_SUCH_JOB_ERROR_MESSAGE = "No such job was found";
    private static final String NOT_READY_ERROR_CODE = "NOT_READY_ERROR_CODE";
    private static final String NOT_READY_ERROR_MESSAGE = "Your file is not ready yet, it's status is: ";
    private static final String FAILED_READ_PARAMS_ERROR_CODE = "FAILED_READ_PARAMS_ERROR_CODE";
    private static final String FAILED_READ_PARAMS_ERROR_MESSAGE = "Failed to read parameters";
    private static final String FAILED_SEND_FILE_ERROR_CODE = "FAILED_SEND_FILE_ERROR_CODE";
    private static final String FAILED_SEND_FILE_ERROR_MESSAGE = "Failed to send file";


    @Autowired
    private JobService jobService;
    @Autowired
    private UserService userService;
    @Autowired
    private Session session;
    @Value("${mail.username}")
    private String username;


    public ExportReturnModel getFile(UUID taskId) {
        var job = jobService.findById(taskId);
        if (job == null) {
            return new ExportReturnModel(NO_SUCH_JOB_ERROR_CODE, NO_SUCH_JOB_ERROR_MESSAGE);
        }
        if (job.getStatus() != TaskStatus.SUCCESS) {
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE + job.getStatus());
        }
        byte[] file;
        try {
            file = FileUtils.readFileToByteArray(new File(taskId.toString()));
        } catch (Exception e) {
            return new ExportReturnModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE);
        }

        var paramsOptional = JsonUtils.readJSON(job.getParams(), ExportCriteriaModel.class);
        return paramsOptional.map(exportCriteriaModel ->
                        new ExportReturnModel(taskId.toString(), exportCriteriaModel.getFileExtension(), file))
                .orElseGet(() ->
                        new ExportReturnModel(FAILED_READ_PARAMS_ERROR_CODE, FAILED_READ_PARAMS_ERROR_MESSAGE));
    }

    public GuidResultModel export(ExportCriteriaModel criteriaModel) {
        var jobModel = new JobModelPost();
        jobModel.setTaskType(criteriaModel.getExportType().getJobType());
        jobModel.setParams(JsonUtils.toString(criteriaModel));
        return jobService.addNew(jobModel);
    }

    public GeneralResultModel sendFileToEmail(UUID jobId) {
        var job = jobService.findById(jobId);
        if (job == null) {
            return new GeneralResultModel(NO_SUCH_JOB_ERROR_CODE, NO_SUCH_JOB_ERROR_MESSAGE);
        }
        if (job.getStatus() != TaskStatus.SUCCESS) {
            return new GeneralResultModel(NOT_READY_ERROR_CODE, NOT_READY_ERROR_MESSAGE + job.getStatus());
        }

        var paramsOptional = JsonUtils.readJSON(job.getParams(), ExportCriteriaModel.class);
        if (paramsOptional.isEmpty()) {
            return new GeneralResultModel(FAILED_READ_PARAMS_ERROR_CODE, FAILED_READ_PARAMS_ERROR_MESSAGE);
        }
        var params = paramsOptional.get();
        var userEmail = userService.getById(CommonUtils.getUserId()).getEmail();
        File file;

        try {
            file = new File(jobId + params.getFileExtension());
            if(!file.exists()){
                var binaryFile = new File(jobId.toString());
                FileUtils.copyFile(binaryFile, file);
            }
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Export");


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            log.error(e);
            return new GeneralResultModel(FAILED_SEND_FILE_ERROR_CODE, FAILED_SEND_FILE_ERROR_MESSAGE);
        }
        return new GeneralResultModel();
    }
}
