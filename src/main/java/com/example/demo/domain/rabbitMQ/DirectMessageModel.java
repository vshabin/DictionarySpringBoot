package com.example.demo.domain.rabbitMQ;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectMessageModel {
    @NotBlank(message = "Queue name should be not empty")
    private String queueName;
    @NotBlank(message = "Message should be not empty")
    private String message;
}
