package com.hust.baseweb.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class TestReceiver {

    private final ObjectMapper objectMapper;

    public TestReceiver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = RabbitProgrammingContestConfig.JUDGE_PROBLEM_QUEUE)
//    public void onMessage(String message) throws Exception {
//        String msg = objectMapper.readValue(message, String.class);
//        System.out.println("Thread " + Thread.currentThread().getId() + ": Received <" + msg + ">");
//        Thread.sleep(500);
//    }
}
