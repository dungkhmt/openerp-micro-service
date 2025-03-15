package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.service;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator.CodeGeneratorService;
import org.springframework.stereotype.Component;

@Component
public class StaffCodeGenerator extends CodeGeneratorService {

    @Override
    public String getPrefix() {
        return "EMP-";
    }

    @Override
    public int lengthSuffix() {
        return 4;
    }
}
