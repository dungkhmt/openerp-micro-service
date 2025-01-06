package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.EditStaff;
import openerp.openerpresourceserver.constant.StaffStatus;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EditStaffRequest {
    private String staffCode;
    private String userLoginId;

    private String fullName;
    private StaffStatus staffStatus;
    private String departmentCode;
    private String jobPositionCode;

    public EditStaff toUseCase(){
        return EditStaff.builder()
                .fullName(fullName)
                .userLoginId(userLoginId)
                .staffCode(staffCode)
                .staffStatus(staffStatus)
                .departmentCode(departmentCode)
                .jobPositionCode(jobPositionCode)
                .build();
    }
}
