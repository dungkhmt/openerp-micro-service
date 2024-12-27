package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;

@Getter
@Setter
@Builder
public class DepartmentModel {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;
}
