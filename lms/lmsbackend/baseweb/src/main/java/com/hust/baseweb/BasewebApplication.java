package com.hust.baseweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableAsync
@EnableCaching
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true) 
@SpringBootApplication
public class BasewebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasewebApplication.class, args);
    }
}
