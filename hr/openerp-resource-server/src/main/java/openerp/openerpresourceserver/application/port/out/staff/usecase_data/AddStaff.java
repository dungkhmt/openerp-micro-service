package openerp.openerpresourceserver.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.StaffModel;

@Data
@Builder
@Getter
@Setter
@Slf4j
public class AddStaff implements UseCase {
    private String fullName;
    private String userLoginId;
    private String email;
    private String staffCode;

    public StaffModel toModel(){
        return StaffModel.builder()
                .fullname(fullName)
                .userLoginId(userLoginId)
                .email(email)
                .staffCode(staffCode)
                .build();
    }
}
