package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.exceptions.ErrorException;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.SendTelegramParams;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.TelegramBot;
import com.example.demo.infrastructure.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Log4j2
@Component
public class TelegramSendJob extends BaseJob {
    private static final String FAILED_READ_PARAMS_ERROR_MESSAGE = "Failed to read parameters";
    private static final String NO_USER_CHAT_ID_ERROR_MESSAGE = "No such user chat id";
    private static final String FAILED_SEND_FILE_ERROR_MESSAGE = "Failed to send file";

    @Autowired
    @Lazy
    private JobService jobService;
    @Autowired
    private TelegramBot telegramBot;

    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("Start sending telegram");
        var paramsOptional = JsonUtils.readJSON(job.getParams(), SendTelegramParams.class);
        if (paramsOptional.isEmpty()) {
            throw new CriticalErrorException(FAILED_READ_PARAMS_ERROR_MESSAGE);
        }
        var params = paramsOptional.get();
        if(params.getUserChatId().isEmpty()) {
            throw new CriticalErrorException(NO_USER_CHAT_ID_ERROR_MESSAGE);
        }

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
            var inputFile = new InputFile();
            inputFile.setMedia(file, params.getAttachmentName() + params.getAttachmentExtension());
            telegramBot.sendMessage(params.getUserChatId(), params.getText(), inputFile);

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
        log.info("Finish sending telegram");
    }

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    @Override
    public TaskType getType() {
        return TaskType.SEND_TELEGRAM;
    }
}
