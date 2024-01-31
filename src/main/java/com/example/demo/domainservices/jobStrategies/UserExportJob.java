package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.common.PageResult;
import com.example.demo.domain.exceptions.CriticalErrorException;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domain.job.params.SendEmailParams;
import com.example.demo.domain.job.params.SendTelegramParams;
import com.example.demo.domain.job.progress.ExportProgress;
import com.example.demo.domain.user.UserCriteriaModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domainservices.JobService;
import com.example.demo.domainservices.UserService;
import com.example.demo.domainservices.jobStrategies.ExportWriters.UserExportExcelWriter;
import com.example.demo.domainservices.jobStrategies.ExportWriters.UserExportWriterInterface;
import com.example.demo.infrastructure.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;


@Log4j2
@Component
public class UserExportJob extends BaseJob {
    private static final String FILE_IS_EMPTY_ERROR_MESSAGE = "Файл результата пуст";
    private static final String FAILED_READ_PARAMS_EXCEPTION_MESSAGE = "Failed to read parameters";
    private static final String EXCEL_EXTENSION = ".xlsx";
    private static final String EMAIL_SUBJECT = "Экспорт пользователей";
    private static final String SEND_TEXT = "Готов ваш экспорт пользователей от ";
    private static final String FILE_NAME_FOR_SEND = "Экспорт_пользователей";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");

    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private JobService jobService;

    @Override
    protected void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        log.info("UserExportJob started");
        ExportCriteriaModel criteriaModel = JsonUtils.readJSON(job.getParams(), ExportCriteriaModel.class)
                .orElseThrow(() -> new CriticalErrorException(FAILED_READ_PARAMS_EXCEPTION_MESSAGE));
        var progress = JsonUtils.readJSON(job.getProgress(), ExportProgress.class)
                .orElse(new ExportProgress(0, 500, 0));

        FileInputStream fileStream;
        try {
            var file = new File(job.getJobId().toString());
            file.createNewFile();
            fileStream = new FileInputStream(job.getJobId().toString());
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }
        UserExportWriterInterface writer;
        switch (criteriaModel.getFileExtension()) {
            case EXCEL_EXTENSION:
                writer = new UserExportExcelWriter(fileStream);
                break;
            default:
                throw new CriticalErrorException("Unknown file extension");
        }

        writer.preWrite();

        PageResult<UserModelReturn> pageResult;
        criteriaModel.setSize(progress.getPageSize());
        criteriaModel.setPageNumber(progress.getLastPage());
        do {
            progress.setLastPage(progress.getLastPage() + 1);
            criteriaModel.setPageNumber(progress.getLastPage());
            pageResult = getUserExportModels(criteriaModel);
            if (pageResult.getTotalCount() == 0) {
                throw new CriticalErrorException(FILE_IS_EMPTY_ERROR_MESSAGE);
            }

            progressMessageModel.setAllCount(pageResult.getTotalCount());
            progress.setAllCount(pageResult.getTotalCount());

            writer.addData(pageResult.getPageContent());

            progressMessageModel.setSuccessCount(progressMessageModel.getSuccessCount() + pageResult.getPageContent().size());

            job.setProgress(JsonUtils.toString(progress));
            jobService.update(job);
        } while (pageResult.getPageContent().size() == criteriaModel.getSize());

        writer.postWrite();

        try {
            FileOutputStream fos = new FileOutputStream(job.getJobId().toString());
            writer.write(fos);
        } catch (Exception e) {
            throw new CriticalErrorException(e.getMessage());
        }

        if (criteriaModel.isSendEmail()) {
            var sendJob = new JobModelPost();
            sendJob.setTaskType(TaskType.SEND_EMAIL);

            var params = new SendEmailParams();
            params.setAttachment(job.getJobId().toString());
            params.setAttachmentExtension(criteriaModel.getFileExtension());
            params.setAttachmentName(FILE_NAME_FOR_SEND);

            params.setTo(userService.getById(job.getCreatorUserId()).getEmail());
            params.setSubject(EMAIL_SUBJECT);
            params.setText(SEND_TEXT + formatter.format(job.getCreatedAt()));

            sendJob.setParams(JsonUtils.toString(params));

            jobService.addNew(sendJob);
        }
        if (criteriaModel.isSendTelegram()) {
            var sendJob = new JobModelPost();
            sendJob.setTaskType(TaskType.SEND_TELEGRAM);

            var params = new SendTelegramParams();
            params.setAttachment(job.getJobId().toString());
            params.setAttachmentExtension(criteriaModel.getFileExtension());
            params.setAttachmentName(FILE_NAME_FOR_SEND);

            params.setUserChatId(userService.getById(job.getCreatorUserId()).getTelegramChatId());
            params.setText(SEND_TEXT + formatter.format(job.getCreatedAt()));

            sendJob.setParams(JsonUtils.toString(params));

            jobService.addNew(sendJob);
        }
        log.info("UserExportJob finished");
    }

    @Override
    public int getMaxAttempt() {
        return 5;
    }

    @Override
    public TaskType getType() {
        return TaskType.USER_EXPORT;
    }

    private PageResult<UserModelReturn> getUserExportModels(ExportCriteriaModel criteriaModel) {
        var userCriteriaModel = new UserCriteriaModel();
        userCriteriaModel.setPageNumber(criteriaModel.getPageNumber());
        userCriteriaModel.setSize(criteriaModel.getSize());
        userCriteriaModel.setRoleFilter(criteriaModel.getByRoleFilter());
        userCriteriaModel.setLoginFilter(criteriaModel.getByLoginFilter());
        userCriteriaModel.setFullNameFilter(criteriaModel.getByFullNameFilter());
        return userService.getPage(userCriteriaModel);
    }


}
