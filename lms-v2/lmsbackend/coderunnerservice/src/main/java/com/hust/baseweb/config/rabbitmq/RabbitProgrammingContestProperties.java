package com.hust.baseweb.config.rabbitmq;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Validated
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.rabbitmq.programming-contest")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RabbitProgrammingContestProperties {

    QueueConfig judgeProblem;

    QueueConfig judgeCustomProblem;

    @PostConstruct
    private void postConstruct() {
        if (this.judgeProblem == null) {
            this.judgeProblem = new QueueConfig(1, 2, 1, 2, 60_000, true);
        }

        if (this.judgeCustomProblem == null) {
            this.judgeCustomProblem = new QueueConfig(1, 2, 1, 2, 60_000, true);
        }
    }

    @Getter
    @Validated
    @AllArgsConstructor
    @ConstructorBinding
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class QueueConfig {

        @Min(1)
//        @Max(15)
        int concurrentConsumers;

        //        @Max(15)
        int maxConcurrentConsumers;

        @Min(1)
        @Max(2)
        int prefetchCount;

        @Min(1)
        int retryLimit;

        @Min(60000)
        int deadMessageTtl;

        boolean autoStartup;
    }
}