package openerp.openerpresourceserver.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.GetDepartment;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetDepartmentRequest {
    private String departmentCode;
    private String departmentName;
    private DepartmentStatus status;
    private PageableRequest pageableRequest;

    public GetDepartment toUseCase(){
        return GetDepartment.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .status(status)
                .pageableRequest(pageableRequest)
                .build();
    }
}
