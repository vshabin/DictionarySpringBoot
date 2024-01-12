package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.infrastructure.repositories.job.JobEntity;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface JobMapper {
    JobModelReturn toJobModelReturn(JobEntity entity);
    JobEntity toJobEntity(JobModelPost model);
}
