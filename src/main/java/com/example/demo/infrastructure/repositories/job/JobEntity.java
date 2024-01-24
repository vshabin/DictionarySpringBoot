package com.example.demo.infrastructure.repositories.job;

import com.example.demo.domain.job.TaskStatus;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import io.ebean.annotation.WhoCreated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "jobs")
public class JobEntity {
    public static final String JOB_ID = "jobId";
    public static final String TASK_TYPE = "taskType";
    public static final String CREATOR_USER_ID = "creatorUserId";
    public static final String STATUS = "status";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ATTEMPT_NUM = "attemptNum";
    public static final String PROGRESS_MESSAGE = "progressMessage";
    public static final String PROGRESS = "progress";
    public static final String PARAMS = "params";
    public static final String LAST_UPDATE_TIME = "lastUpdateTime";
    public static final String MIN_START_TIME = "minStartTime";
    public static final String CREATED_AT = "created_at";

    @Id
    @Column(name = JOB_ID)
    private UUID jobId;

    @Column(name = TASK_TYPE)
    @NotBlank
    private String taskType;

    @Column(name = CREATOR_USER_ID)
    @WhoCreated
    private UUID creatorUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = STATUS)
    @NotBlank
    private TaskStatus status;

    @Column(name = ERROR_MESSAGE)
    private String taskErrorMessage;

    @Column(name = ATTEMPT_NUM)
    @Min(0)
    private int attemptNum;

    @Column(name = PROGRESS_MESSAGE)
    private String progressMessage;

    @Column(name = PROGRESS)
    private String progress;

    @Column(name = PARAMS)
    private String params;

    @Column(name = LAST_UPDATE_TIME)
    @WhenModified
    private LocalDateTime lastUpdateTime;

    @Column(name = MIN_START_TIME)
    private LocalDateTime minStartTime;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

}
