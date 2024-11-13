package com.hust.baseweb.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseRabbitListener {

    abstract void onMessage(
        Message message, String messageBody, Channel channel, Integer deliveryCount
    ) throws Exception;

    abstract void retryMessage(Message message, String messageBody, Channel channel) throws Exception;

    abstract void sendMessageToDeadLetterQueue(Message message, Channel channel) throws Exception;
}
