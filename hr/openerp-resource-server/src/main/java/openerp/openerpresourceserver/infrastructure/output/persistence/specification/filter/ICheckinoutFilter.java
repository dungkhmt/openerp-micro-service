package openerp.openerpresourceserver.infrastructure.output.persistence.specification.filter;

import openerp.openerpresourceserver.constant.CheckinoutType;

import java.time.LocalDate;

public interface ICheckinoutFilter {
    String getUserId();
    LocalDate getDate();
    CheckinoutType getType();
}
