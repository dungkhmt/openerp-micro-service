package com.hust.openerp.taskmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class AppConfig {
    @Value("${app.endpoint.client}")
    private String clientUrl;
}
