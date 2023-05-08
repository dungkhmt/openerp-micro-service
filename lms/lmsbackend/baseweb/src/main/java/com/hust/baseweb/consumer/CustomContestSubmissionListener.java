package com.hust.baseweb.consumer;

import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestProperties;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_CUSTOM_PROBLEM_DL;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.DEAD_LETTER_EXCHANGE;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.JUDGE_CUSTOM_PROBLEM_QUEUE;

@Slf4j
@Component
public class CustomContestSubmissionListener extends BaseRabbitListener {

    private final ProblemTestCaseService problemTestCaseService;

    private final RabbitProgrammingContestProperties rabbitConfig;

    public CustomContestSubmissionListener(
        ProblemTestCaseService problemTestCaseService,
        RabbitProgrammingContestProperties rabbitConfig
    ) {
        this.problemTestCaseService = problemTestCaseService;
        this.rabbitConfig = rabbitConfig;
    }

    @Override
    @RabbitListener(queues = JUDGE_CUSTOM_PROBLEM_QUEUE)
    public void onMessage(
        Message message, String messageBody, Channel channel,
        @Header(required = false, name = "x-delivery-count") Integer deliveryCount
    ) throws Exception {
        if (deliveryCount == null || deliveryCount < rabbitConfig.getRetryLimit()) {
            retryMessage(message, messageBody, channel);
        } else {
            sendMessageToDeadLetterQueue(message, channel);
        }
    }

    @Override
    protected void retryMessage(Message message, String messageBody, Channel channel) throws IOException {
        try {
            UUID contestSubmissionId = UUID.fromString(messageBody);
            problemTestCaseService.evaluateCustomProblemSubmission(contestSubmissionId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("An error happened when processing message (JUDGE_CUSTOM_PROBLEM_QUEUE). Retry later");
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @Override
    protected void sendMessageToDeadLetterQueue(Message message, Channel channel) throws IOException {
        channel.basicPublish(
            DEAD_LETTER_EXCHANGE,
            JUDGE_CUSTOM_PROBLEM_DL,
            new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
            message.getBody());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
