package com.hust.baseweb.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.education.quiztest.service.QuizTestService;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestProperties;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.hust.baseweb.config.rabbitmq.QuizRoutingKey.QUIZ_DL;
import static com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig.*;

@Component
public class QuizSubmissionListener extends BaseRabbitListener {

    private final ObjectMapper objectMapper;
    //private final ProblemTestCaseService problemTestCaseService;
    private final QuizTestService quizTestService;


    private final RabbitProgrammingContestProperties rabbitConfig;

    public QuizSubmissionListener(
        ObjectMapper objectMapper,
        ProblemTestCaseService problemTestCaseService,
        QuizTestService quizTestService,
        RabbitProgrammingContestProperties rabbitConfig
    ) {
        this.objectMapper = objectMapper;
        this.quizTestService = quizTestService;
        //this.problemTestCaseService = problemTestCaseService;
        this.rabbitConfig = rabbitConfig;
    }

    @Override
    @RabbitListener(queues = QUIZ_QUEUE)
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
            UUID quizId = UUID.fromString(messageBody);
            //problemTestCaseService.evaluateCustomProblemSubmission(quizId);
            quizTestService.updateFromQuizTestExecutionSubmission(quizId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @Override
    protected void sendMessageToDeadLetterQueue(Message message, Channel channel) throws IOException {
        channel.basicPublish(
            QUIZ_DEAD_LETTER_EXCHANGE,
            QUIZ_DL,
            new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
            message.getBody());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
