package com.example.demo.domainservices;

import com.example.demo.config.ProcessorInfo;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class JobService {

    private final static String ATTEMPTS_ARE_OVER_ERROR_MESSAGE = "Task failed too many times. We finish its execution";
    private final static String NO_SUCH_STRATEGY_ERROR_MESSAGE = "No such strategy";
    private final static String JOB_ALREADY_EXIST_ERROR_CODE = "JOB_ALREADY_EXIST_ERROR_CODE";
    private final static String JOB_ALREADY_EXIST_ERROR_MESSAGE = "Job with same task already exists";
    private final Map<TaskType, JobInterface> strategies;

    @Autowired
    private JobRepository repository;

    @Qualifier("jobs")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private ProcessorInfo processorInfo;

    public JobService(Collection<JobInterface> jobImpls) {
        this.strategies = jobImpls.stream().collect(Collectors.toMap(JobInterface::getType, Function.identity()));
    }

    @Scheduled(fixedRate = 5000, initialDelay = 1000)
    private void checkJobs() {
        var threadCount = executor.getCorePoolSize() - executor.getActiveCount();
        if (threadCount == 0) {
            return;
        }

        var jobsForExecute = repository.getJobsForExecute(threadCount);
        var jobs = new ArrayList<JobModelReturn>();
        jobsForExecute.forEach(job -> {
                    job.setProcessor(processorInfo.getComputerName());
                    var result = repository.update(job);
                    if (result.getErrorCode() == null) {
                        jobs.add(result);
                    }
                }
        );

        for (JobModelReturn job : jobs) {
            var strategy = strategies.get(job.getTaskType());
            if (strategy == null) {
                job.setStatus(TaskStatus.FAILED);
                job.setTaskErrorMessage(NO_SUCH_STRATEGY_ERROR_MESSAGE);
                job.setContext(null);
                continue;
            }
            job.setAttemptNum(job.getAttemptNum() + 1);
            if (job.getAttemptNum() > strategy.getMaxAttempt()) {
                job.setStatus(TaskStatus.ATTEMPTS_ARE_OVER);
                job.setTaskErrorMessage(ATTEMPTS_ARE_OVER_ERROR_MESSAGE);
                job.setContext(null);
                continue;
            }
            var future = CompletableFuture.runAsync(() -> {
                SecurityContextHolder.getContext().setAuthentication(CommonUtils.getSchedulerAuth());
                strategy.run(job);
            }, executor);
//            executor.execute(() -> {
//                SecurityContextHolder.getContext().setAuthentication(CommonUtils.getSchedulerAuth());
//                strategy.run(job);
//            });
            job.setStatus(TaskStatus.IS_RUNNING);
        }
        repository.updateList(jobs);

    }

    @EventListener(ApplicationReadyEvent.class)
    private void rerunIsRunning() {
        var jobs = repository.getIsRunningOfProcessor();
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
        if (!model.getTaskType().isParallelize()) {
            modelReturn.setContext(model.getTaskType().name() + " : " + CommonUtils.getUserId());
        }
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
