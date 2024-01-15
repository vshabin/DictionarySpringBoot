package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.export.ExportCriteriaModel;
import com.example.demo.domain.export.ExportReturnModel;
import com.example.demo.domain.export.ExportType;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.domain.job.TaskStatus;
import com.example.demo.domain.job.TaskType;
import com.example.demo.domainservices.exportStrategies.ExportInterface;
import com.example.demo.domainservices.jobStrategies.JobInterface;
import com.example.demo.domainservices.jobStrategies.TestJobImpl;
import com.example.demo.infrastructure.repositories.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    JobRepository repository;

    @Qualifier("jobs")
    @Autowired
    ThreadPoolTaskExecutor executor;
    private final String NO_SUCH_STRATEGY_ERROR_CODE="NO_SUCH_STRATEGY_ERROR_CODE";
    private final String NO_SUCH_STRATEGY_ERROR_MESSAGE="No such strategy";

    private final Map<TaskType, JobInterface> strategies;

    public JobService(Collection<JobInterface> jobImpls) {
        this.strategies = jobImpls.stream()
                .collect(Collectors.toMap(JobInterface::getType, Function.identity()));
    }

//    public GuidResultModel getFile(JobModelPost model){
//        var strategy = strategies.get(model.getTaskType());
//        if (strategy == null) {
//            return new GuidResultModel(NO_SUCH_STRATEGY_ERROR_CODE,NO_SUCH_STRATEGY_ERROR_MESSAGE);
//        }
//        return strategies.get(model.getTaskType()).addTask(model);
//    }
    @Scheduled(fixedRate = 10000, initialDelay = 1000)
    private void checkJobs() {
        var jobs = repository.getUnsuccessfulJobs();
        var startedJobs = new ArrayList<JobModelReturn>();
        for (JobModelReturn job : jobs) {
            if(executor.getQueueCapacity() - executor.getQueueSize()>0){
                try {
                    var strategy = strategies.get(job.getTaskType());
                    if(strategy == null){
                        job.setStatus(TaskStatus.FAILED);
                        job.setTaskErrorMessage(NO_SUCH_STRATEGY_ERROR_CODE);
                        repository.update(job);
                        continue;
                    }
                    strategy.setParams(job.getParams(), job.getJobId());
                    executor.execute(strategy);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
                job.setStatus(TaskStatus.IS_RUNNING);
                startedJobs.add(job);
            }
        }
        repository.updateList(startedJobs);

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

    public GuidResultModel makeSuccess(UUID id){
        return repository.makeSuccess(id);
    }
}
