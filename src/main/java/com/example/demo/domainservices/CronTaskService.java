package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.crontasks.CronTaskPostModel;
import com.example.demo.domain.crontasks.CronTaskReturnModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.infrastructure.CommonUtils;
import com.example.demo.infrastructure.repositories.cronTask.CronTaskRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class CronTaskService {
    @Autowired
    CronTaskRepository repository;
    @Autowired
    JobService jobService;

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 1000)
    private void checkJobs() {
        var tasks = repository.getTasks();
        for (CronTaskReturnModel task : tasks) {
            var expression = CronExpression.parse(task.getCronExpression());
            var next = expression.next(LocalDateTime.now());
            if (next == null) {
                continue;
            }
            while (next.isBefore(LocalDateTime.now().plus(Duration.ofMinutes(6)))) {
                if (jobService.thereIsSameTask(task.getTaskName(), next)) {
                    next = expression.next(next);
                    continue;
                }
                var jobModel = new JobModelPost();
                jobModel.setTaskType(task.getTaskName());
                jobModel.setMinStartTime(next);

                SecurityContextHolder.getContext().setAuthentication(CommonUtils.getSchedulerAuth());

                jobService.addNew(jobModel);
                log.info(MessageFormat.format("CronTask создан новый джоб: {0}", jobModel.getMinStartTime()));
                next = expression.next(next);

            }
        }
    }

    public CronTaskReturnModel findById(UUID id) {
        return repository.findById(id);
    }

    public GuidResultModel addNew(CronTaskPostModel model) {
        return repository.save(model);
    }
}
