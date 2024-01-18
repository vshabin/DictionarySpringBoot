package com.example.demo.domainservices.jobStrategies;

import com.example.demo.domain.exceptions.ErrorException;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.ProgressMessageModel;
import com.example.demo.domain.job.TaskType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class AssociationsExportJob extends BaseJob {
    @Override
    public int getMaxAttempt() {
        return 3;
    }

    @Override
    public void internalRun(JobModelReturn job, ProgressMessageModel progressMessageModel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
        for (int i = 0; i < 1; i++) {
            log.info(job.getMinStartTime().format(formatter));
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (Exception e) {
                throw new ErrorException(e.getMessage());
            }
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.ASSOCIATIONS_EXPORT;
    }
}
