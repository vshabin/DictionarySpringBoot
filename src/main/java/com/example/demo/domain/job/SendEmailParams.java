package com.example.demo.domain.job;

import lombok.Data;

@Data
public class SendEmailParams {
    private String to;
    private String subject;
    private String text;
    private String attachment;
    private String attachmentExtension;
}
