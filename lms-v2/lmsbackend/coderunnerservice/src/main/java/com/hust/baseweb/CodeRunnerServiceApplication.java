package com.hust.baseweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.hust.baseweb", "vn.edu.hust.soict.judge0client"})
@ConfigurationPropertiesScan()
public class CodeRunnerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeRunnerServiceApplication.class, args);
    }

}
