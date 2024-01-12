package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.infrastructure.repositories.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JobService {
    @Autowired
    JobRepository repository;

    @Scheduled(fixedRate = 1000)
    private void checkJobs(){

    }

    public JobModelReturn findById(UUID id){
        return repository.findById(id);
    }

    public GuidResultModel save(JobModelPost model){
        return repository.save(model);
    }
}
