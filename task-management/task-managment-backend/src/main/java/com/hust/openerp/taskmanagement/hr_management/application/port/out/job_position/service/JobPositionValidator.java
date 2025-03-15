package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.service;

import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.NameValidatorUtils;

@DomainComponent
@Slf4j
public class JobPositionValidator {

    public void validateJobName(String name) {
        NameValidatorUtils.validateName(name);
    }
}
