package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.jobStrategies.JobInterface;
import com.example.demo.infrastructure.CommonUtils;
import com.example.demo.infrastructure.repositories.job.JobRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final static String ATTEMPTS_ARE_OVER_ERROR_MESSAGE = "Task failed too many times. We finish its execution";
    private final static String NO_SUCH_STRATEGY_ERROR_MESSAGE = "No such strategy";
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
            if (executor.getQueueCapacity() - executor.getQueueSize() > 0) {
                var strategy = strategies.get(job.getTaskType());
                if (strategy == null) {
                    job.setStatus(TaskStatus.FAILED);
                    job.setTaskErrorMessage(NO_SUCH_STRATEGY_ERROR_MESSAGE);
                    continue;
                }
                job.setAttemptNum(job.getAttemptNum() + 1);
                if (job.getAttemptNum() >= strategy.getMaxAttempt()) {
                    job.setStatus(TaskStatus.ATTEMPTS_ARE_OVER);
                    job.setTaskErrorMessage(ATTEMPTS_ARE_OVER_ERROR_MESSAGE);
                    continue;
                }
                executor.execute(() -> {
                    SecurityContextHolder.getContext().setAuthentication(CommonUtils.getSchedulerAuth());
                    strategy.run(job);
                });
                job.setStatus(TaskStatus.IS_RUNNING);
            }
        }
        repository.updateList(jobs);

    }

    @EventListener(ApplicationReadyEvent.class)
    private void rerunIsRunning() {
        var jobs = repository.getIsRunning();
        for (JobModelReturn job : jobs) {
            var strategy = strategies.get(job.getTaskType());
            if (strategy == null) {
                job.setStatus(TaskStatus.FAILED);
                job.setTaskErrorMessage(NO_SUCH_STRATEGY_ERROR_MESSAGE);
                continue;
            }
            executor.execute(() -> strategy.run(job));
        }
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
    public GuidResultModel addNew(JobModelPost model, UUID taskId) {
        var modelReturn = new JobModelReturn();
        modelReturn.setJobId(taskId);
        modelReturn.setTaskType(model.getTaskType());
        modelReturn.setParams(model.getParams());
        modelReturn.setMinStartTime(model.getMinStartTime());
        modelReturn.setStatus(TaskStatus.NEW);
        return repository.save(modelReturn);
    }
    public JobModelReturn update(JobModelReturn model) {
        return repository.update(model);
    }

    public boolean getIsCanceled(UUID id) {
        return repository.getIsCanceled(id);
    }

    public GuidResultModel cancel(UUID id) {
        return repository.cancel(id);
    }

    public boolean thereIsSameTask(TaskType taskName, LocalDateTime next) {
        return repository.thereIsSameTask(taskName, next);
    }
}
