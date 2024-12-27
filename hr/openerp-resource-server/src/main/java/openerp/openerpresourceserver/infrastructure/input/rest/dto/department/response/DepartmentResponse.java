package openerp.openerpresourceserver.infrastructure.input.rest.dto.department.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.model.DepartmentModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class DepartmentResponse {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public static DepartmentResponse fromModel(DepartmentModel model) {
        return DepartmentResponse.builder()
                .departmentCode(model.getDepartmentCode())
                .departmentName(model.getDepartmentName())
                .description(model.getDescription())
                .status(model.getStatus())
                .build();
    }
}
