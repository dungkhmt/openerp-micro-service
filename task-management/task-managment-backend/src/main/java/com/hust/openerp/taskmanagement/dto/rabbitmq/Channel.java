package com.hust.openerp.taskmanagement.dto.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    private InAppChannel inApp;

    private MailChannel email;
}
