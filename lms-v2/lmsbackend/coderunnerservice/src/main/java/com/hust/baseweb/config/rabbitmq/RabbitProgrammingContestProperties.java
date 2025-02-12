package com.hust.baseweb.config.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Validated
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.rabbitmq.programming-contest")
public class RabbitProgrammingContestProperties {

    @Min(1)
    private int concurrentConsumers;

    @Max(10)
    private int maxConcurrentConsumers;

    @Min(1)
    @Max(2)
    private int prefetchCount;

    @Min(1)
    private int retryLimit;

    @Min(60000)
    private int deadMessageTtl;

}

