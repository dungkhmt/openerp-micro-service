package com.hust.openerp.taskmanagement.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hust.openerp.taskmanagement.dto.NotificationMessage;
import com.hust.openerp.taskmanagement.dto.rabbitmq.MailChannel;
import com.hust.openerp.taskmanagement.service.MailService;

import jakarta.mail.MessagingException;
import lombok.Setter;

@Component
public class MailNotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(MailNotificationConsumer.class);

    private MessageConverter messageConverter;

    @Autowired
    @Setter
    private MailService mailService;

    public MailNotificationConsumer(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @RabbitListener(queues = "#{queueEmail.name}")
    public void onMessage(Message message) {
        try {
            NotificationMessage notificationMessage = (NotificationMessage) messageConverter.fromMessage(message);

            MailChannel mailChannel = notificationMessage.getChannels().getEmail();
            var mimeMessageHelper = mailService.createMimeMessage(mailChannel.getTo(), mailChannel.getCc(),
                    mailChannel.getBcc(),
                    mailChannel.getSubject(), mailChannel.getBody(), mailChannel.getIsHtml(), mailChannel.getReplyTo(),
                    mailChannel.getReplyPersonal());

            mailService.sendMultipleMimeMessages(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.error(e.toString());
            // TODO: retry

        }
    }
}
