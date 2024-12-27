package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StaffJobPositionModel {
    private String jobName;
    private String startDate;
}
