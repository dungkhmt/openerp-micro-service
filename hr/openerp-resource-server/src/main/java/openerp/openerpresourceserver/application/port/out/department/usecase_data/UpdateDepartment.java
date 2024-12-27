package openerp.openerpresourceserver.application.port.out.department.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.DepartmentModel;

@Data
@Builder
@Getter
@Setter
public class UpdateDepartment implements UseCase {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public DepartmentModel toModel() {
        return DepartmentModel.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .description(description)
                .status(status)
                .build();
    }
}
