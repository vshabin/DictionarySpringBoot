package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.jobStrategies.JobInterface;
import com.example.demo.infrastructure.repositories.job.JobRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class JobService {
    private final String ATTEMPTS_ARE_OVER_ERROR_MESSAGE = "Task failed 5 times. We finish its execution";
    private final String NO_SUCH_STRATEGY_ERROR_MESSAGE = "No such strategy";
    private final Map<TaskType, JobInterface> strategies;
    @Autowired
    JobRepository repository;
    @Qualifier("jobs")
    @Autowired
    ThreadPoolTaskExecutor executor;

    public JobService(Collection<JobInterface> jobImpls) {
        this.strategies = jobImpls.stream().collect(Collectors.toMap(JobInterface::getType, Function.identity()));
    }

    @Scheduled(fixedRate = 10000, initialDelay = 1000)
    private void checkJobs() {
        var jobs = repository.getUnfinishedJobs();
        for (JobModelReturn job : jobs) {
            if (job.getMinStartTime() != null) {
                if (job.getMinStartTime().isAfter(LocalDateTime.now())) {
                    continue;
                }
            }
            if (executor.getQueueCapacity() - executor.getQueueSize() > 0) {
                job.setAttemptNum(job.getAttemptNum() + 1);
                if (job.getAttemptNum() >= 5) {
                    job.setStatus(TaskStatus.ATTEMPTS_ARE_OVER);
                    continue;
                }
                var strategy = strategies.get(job.getTaskType());
                if (strategy == null) {
                    job.setStatus(TaskStatus.FAILED);
                    job.setTaskErrorMessage(NO_SUCH_STRATEGY_ERROR_MESSAGE);
                    continue;
                }
                try {
                    strategy.setParams(job);
                } catch (Exception e) {
                    job.setStatus(TaskStatus.FAILED);
                    job.setTaskErrorMessage(e.getMessage());
                    log.error(e.getMessage(), e);
                    continue;
                }
                executor.execute(strategy);
                job.setStatus(TaskStatus.IS_RUNNING);
            }
        }
        repository.updateList(jobs);

    }

    public JobModelReturn findById(UUID id) {
        return repository.findById(id);
    }

    public GuidResultModel addNew(JobModelPost model) {
        var modelReturn = new JobModelReturn();
        modelReturn.setTaskType(model.getTaskType());
        modelReturn.setParams(model.getParams());
        modelReturn.setMinStartTime(model.getMinStartTime());
        modelReturn.setStatus(TaskStatus.NEW);
        return repository.save(modelReturn);
    }

    public JobModelReturn update(JobModelReturn model) {
        return repository.update(model);
    }
}