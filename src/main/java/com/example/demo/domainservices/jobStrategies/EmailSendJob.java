package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.exceptions.ErrorException;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.SendEmailParams;
import com.example.demo.domainservices.JobService;
import com.example.demo.infrastructure.JsonUtils;
import jakarta.mail.Message;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;

@Log4j2
@Component
public class EmailSendJob extends BaseJob {
    private static final String FAILED_READ_PARAMS_ERROR_MESSAGE = "Failed to read parameters";
    private static final String FAILED_SEND_FILE_ERROR_MESSAGE = "Failed to send file";

    @Autowired
    private Session mailSession;
    @Autowired
    @Lazy
    private JobService jobService;
    @Value("${mail.username}")
    private String username;

    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("Start sending email");
        var paramsOptional = JsonUtils.readJSON(job.getParams(), SendEmailParams.class);
        if (paramsOptional.isEmpty()) {
            throw new CriticalErrorException(FAILED_READ_PARAMS_ERROR_MESSAGE);
        }
        var params = paramsOptional.get();

        progressMessageModel.setAllCount(1);
        job.setProgress(JsonUtils.toString(progressMessageModel));
        job.setProgressMessage(JsonUtils.toString(progressMessageModel));
        jobService.update(job);

        File file;
        try {
            file = new File(params.getAttachment() + params.getAttachmentExtension());
            if (!file.exists()) {
                var binaryFile = new File(params.getAttachment());
                FileUtils.copyFile(binaryFile, file);
            }
            var message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(params.getTo()));
            message.setSubject(params.getSubject());

            var mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(params.getText(), "text/plain; charset=utf-8");

            var attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.attachFile(file);
            attachmentBodyPart.setFileName(params.getAttachmentName() + params.getAttachmentExtension());

            var multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            progressMessageModel.setSuccessCount(1);
            job.setProgress(JsonUtils.toString(progressMessageModel));
            job.setProgressMessage(JsonUtils.toString(progressMessageModel));

            jobService.update(job);
        } catch (Exception e) {
            log.error(e);
            progressMessageModel.setErrorCount(1);
            job.setProgress(JsonUtils.toString(progressMessageModel));
            job.setProgressMessage(JsonUtils.toString(progressMessageModel));
            jobService.update(job);
            throw new ErrorException(FAILED_SEND_FILE_ERROR_MESSAGE);
        }
        log.info("Finish sending email");
    }

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    @Override
    public TaskType getType() {
        return TaskType.SEND_EMAIL;
    }
}
