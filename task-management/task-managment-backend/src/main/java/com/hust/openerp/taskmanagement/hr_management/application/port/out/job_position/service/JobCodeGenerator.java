package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.service;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class JobCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "JOB-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
