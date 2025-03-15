package com.hust.openerp.taskmanagement;

import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {DomainComponent.class})
    }
)
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }
}
