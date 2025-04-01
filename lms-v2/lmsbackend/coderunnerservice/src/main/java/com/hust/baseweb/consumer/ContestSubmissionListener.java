package com.hust.baseweb.consumer;

import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestConfig;
import com.hust.baseweb.config.rabbitmq.RabbitProgrammingContestProperties;
import com.hust.baseweb.service.Judge0ProblemTestCaseServiceImpl;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static com.hust.baseweb.config.rabbitmq.ProblemContestRoutingKey.JUDGE_PROBLEM_DL;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ContestSubmissionListener extends BaseRabbitListener {

    Judge0ProblemTestCaseServiceImpl problemTestCaseService;

    RabbitProgrammingContestProperties rabbitConfig;

    @Override
    @RabbitListener(queues = RabbitProgrammingContestConfig.JUDGE_PROBLEM_QUEUE,
            containerFactory = "judgeProblemListenerContainerFactory")
    public void onMessage(
            Message message, String messageBody, Channel channel,
            @Header(required = false, name = "x-delivery-count") Integer deliveryCount
    ) throws Exception {
        if (deliveryCount == null || deliveryCount < rabbitConfig.getJudgeProblem().getRetryLimit()) {
            retryMessage(message, messageBody, channel);
        } else {
            sendMessageToDeadLetterQueue(message, channel);
        }
    }

    @Override
    protected void retryMessage(Message message, String messageBody, Channel channel) throws IOException {
//        if (true) {
//            System.out.println("Nack");
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//            return;
//        }

        try {
            UUID contestSubmissionId = UUID.fromString(messageBody);
            problemTestCaseService.submitContestProblemTestCaseByTestCaseWithFileProcessor(contestSubmissionId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("An error happened when processing message (JUDGE_PROBLEM_QUEUE). Retry later");
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @Override
    protected void sendMessageToDeadLetterQueue(Message message, Channel channel) throws IOException {
        channel.basicPublish(
                RabbitProgrammingContestConfig.DEAD_LETTER_EXCHANGE,
                JUDGE_PROBLEM_DL,
                new AMQP.BasicProperties.Builder().deliveryMode(2).build(),
                message.getBody());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
