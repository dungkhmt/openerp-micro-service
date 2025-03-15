package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.service;
import openerp.openerpresourceserver.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class CheckpointConfigureCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "CPC-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
