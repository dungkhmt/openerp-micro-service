package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.service;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.exception.InvalidParameterException;

@DomainComponent
@Slf4j
public class DepartmentValidator {

    public void validateDepartmentName(String name) {
        if (!name.matches("^[a-zA-Z0-9]+(\\s[a-zA-Z0-9]+)*$")) {
            log.error("Department name contains invalid characters or invalid spacing");
            throw new InvalidParameterException("Department name contains invalid characters or invalid spacing." +
                    " Only letters, numbers, and single spaces between words are allowed.");
        }

    }
}
