package openerp.openerpresourceserver.infrastructure.output.persistence.projection;

import java.time.LocalDate;

public interface StaffDepartmentProjection {
    String getUserLoginId();
    String getDepartmentCode();
    String getDepartmentName();
    String getDescription();
    String getStatus();
    LocalDate getFromDate();
    LocalDate getThruDate();
}
