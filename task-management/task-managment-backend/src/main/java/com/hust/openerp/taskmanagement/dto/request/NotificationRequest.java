package com.hust.openerp.taskmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationRequest {
    private String toUser;
    private String content;
    private String url;
}
