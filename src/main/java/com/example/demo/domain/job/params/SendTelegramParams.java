package com.example.demo.domain.job.params;

import lombok.Data;

@Data
public class SendTelegramParams {
    private String userChatId;
    private String text;
    private String attachment;
    private String attachmentName;
    private String attachmentExtension;
}
