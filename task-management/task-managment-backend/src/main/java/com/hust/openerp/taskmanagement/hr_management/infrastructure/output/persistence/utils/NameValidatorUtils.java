package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.exception.InvalidParameterException;

@Slf4j
public class NameValidatorUtils {
    public static void validateName(String name) {
        if (!name.matches("^[\\p{L}]+(\\s[\\p{L}]+)*$")) {
            log.error("Staff name contains invalid characters or invalid spacing");
            throw new InvalidParameterException("Staff name contains invalid characters or invalid spacing." +
                    " Only letters and single spaces between words are allowed.");
        }
    }
}
