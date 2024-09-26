package com.hust.openerp.taskmanagement.dto.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailChannel {
    private String[] to;

    private String[] cc;

    private String[] bcc;

    private String subject;

    private String body;

    private Boolean isHtml;

    private String replyTo;

    private String replyPersonal;
}
