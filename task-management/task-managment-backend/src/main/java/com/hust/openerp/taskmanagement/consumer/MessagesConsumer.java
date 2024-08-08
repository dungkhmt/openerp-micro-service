package com.hust.openerp.taskmanagement.consumer;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.hust.openerp.taskmanagement.service.MailService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@AllArgsConstructor
@DependsOn({ "jsonMessageConverter" })
@ConditionalOnProperty(value = "app.feature.rabbitmq", havingValue = "true")
public class MessagesConsumer {
    @Autowired
    private MessageConverter messageConverter;

    @Bean
    public MailNotificationConsumer mailNotificationConsumer(MailService mailService) {
        return new MailNotificationConsumer(messageConverter, mailService);
    }

    @Bean
    public InAppNotificationConsumer inAppNotificationConsumer() {
        return new InAppNotificationConsumer(messageConverter);
    }
}
