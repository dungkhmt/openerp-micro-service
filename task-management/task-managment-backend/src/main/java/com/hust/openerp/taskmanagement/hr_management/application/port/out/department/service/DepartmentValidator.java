package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.service;

import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
public class DepartmentValidator {

    public void validateDepartmentName(String name) {
        /*if (!name.matches("^[\\p{L}\\p{N}]+(\\s[\\p{L}\\p{N}]+)*$")) {
            log.error("Department name contains invalid characters or invalid spacing");
            throw new InvalidParameterException("Department name contains invalid characters or invalid spacing." +
                " Only Unicode letters (including Vietnamese), numbers, and single spaces between words are allowed.");
        }*/
    }
}
