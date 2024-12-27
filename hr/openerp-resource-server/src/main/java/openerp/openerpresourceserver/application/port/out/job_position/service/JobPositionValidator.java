package openerp.openerpresourceserver.application.port.out.job_position.service;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.infrastructure.output.persistence.utils.NameValidatorUtils;

@DomainComponent
@Slf4j
public class JobPositionValidator {

    public void validateJobName(String name) {
        NameValidatorUtils.validateName(name);
    }
}
