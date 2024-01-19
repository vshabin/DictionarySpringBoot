package com.example.demo.infrastructure.repositories.cronTask;

import io.ebean.annotation.WhoCreated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Data
@Table(name = "crontasks")
public class CronTaskEntity {
    public static final String TASK_ID = "cronTaskId";
    public static final String TASK_NAME = "taskType";
    public static final String CRON_EXPRESSION = "cronExpression";

    @Id
    @Column(name = TASK_ID)
    private UUID taskId;

    @Column(name = TASK_NAME)
    @NotBlank
    private String taskName;

    @Column(name = CRON_EXPRESSION)
    @NotBlank
    private String cronExpression;
}
