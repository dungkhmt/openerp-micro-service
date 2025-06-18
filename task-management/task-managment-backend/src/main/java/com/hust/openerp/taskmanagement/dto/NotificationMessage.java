package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.dto.rabbitmq.Channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String userId;

    private String organizationCode;

    private Channel channels;
}
