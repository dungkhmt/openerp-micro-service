package openerp.openerpresourceserver.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.StaffModel;

@Data
@Builder
@Getter
@Setter
public class EditStaff implements UseCase {
    private String userLoginId;
    private String staffCode;
    private String fullName;
    private StaffStatus staffStatus;
    private String departmentCode;
    private String jobPositionCode;

    public StaffModel toModel(){
        return StaffModel.builder()
                .fullname(fullName)
                .userLoginId(userLoginId)
                .staffCode(staffCode)
                .status(staffStatus)
                .build();
    }
}
