package com.example.demo.domain.job.params;

import lombok.Data;

@Data
public class SendEmailParams {
    private String to;
    private String subject;
    private String text;
    private String attachment;
    private String attachmentName;
    private String attachmentExtension;
}
