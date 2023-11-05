package com.hust.openerp.taskmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class GetNotificationsOM {

    private Page<NotificationDTO> notifications;

    private long numUnRead;
}
