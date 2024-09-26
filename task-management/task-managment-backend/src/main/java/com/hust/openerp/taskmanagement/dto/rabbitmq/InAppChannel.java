package com.hust.openerp.taskmanagement.dto.rabbitmq;

import java.util.List;

import com.hust.openerp.taskmanagement.dto.NotificationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InAppChannel {
    private List<NotificationDTO> notifications;
    private OperationType type;
}
