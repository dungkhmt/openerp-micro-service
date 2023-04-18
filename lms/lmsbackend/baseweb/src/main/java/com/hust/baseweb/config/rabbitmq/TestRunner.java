package com.hust.baseweb.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public TestRunner(
        RabbitTemplate rabbitTemplate, ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

   @Override
   public void run(String... args) throws Exception {
    //    System.out.println("Sending messages...");
    //     for (int i = 0; i < 100; i++) {
    //         String msg = "Hello" + (i + 1) + " from RabbitMQ!";
    //         rabbitTemplate.convertAndSend(
       //             RabbitProgrammingContestConfig.EXCHANGE,
       //             ProblemContestRoutingKey.JUDGE_PROBLEM,
    //             objectMapper.writeValueAsString(msg)
    //         );
    //     }
   }
}
