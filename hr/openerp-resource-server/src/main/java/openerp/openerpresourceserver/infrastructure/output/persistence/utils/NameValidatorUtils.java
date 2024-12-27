package openerp.openerpresourceserver.infrastructure.output.persistence.utils;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.exception.InvalidParameterException;

@Slf4j
public class NameValidatorUtils {
    public static void validateName(String staffName) {
        if (!staffName.matches("^[a-zA-Z]+(\\s[a-zA-Z]+)*$")) {
            log.error("Staff name contains invalid characters or invalid spacing");
            throw new InvalidParameterException("Department name contains invalid characters or invalid spacing." +
                    " Only letters and single spaces between words are allowed.");
        }
    }
}
