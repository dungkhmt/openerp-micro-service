package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.service;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.infrastructure.output.persistence.utils.NameValidatorUtils;

@DomainComponent
@Slf4j
public class StaffValidator {
    public void validateStaffName(String staffName) {
        NameValidatorUtils.validateName(staffName);
    }
}
