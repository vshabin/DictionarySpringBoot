package com.example.demo.infrastructure.repositories.MapperInterfaces;

import com.example.demo.domain.job.JobModelPost;
import com.example.demo.domain.job.JobModelReturn;
import com.example.demo.infrastructure.repositories.job.JobEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface JobMapper {
    JobModelReturn toJobModelReturn(JobEntity entity);
    JobEntity toJobEntity(JobModelReturn model);

    List<JobModelReturn> toJobModelReturnList(List<JobEntity> result);

    List<JobEntity> toJobEntityList(List<JobModelReturn> jobs);
}
