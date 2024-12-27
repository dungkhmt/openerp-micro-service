package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.domain.model.StaffModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class StaffResponse {
    private String staffCode;
    private String userLoginId;
    private String fullname;
    private StaffStatus status;
    private String email;

    public static StaffResponse fromModel(StaffModel model) {
        return StaffResponse.builder()
                .staffCode(model.getStaffCode())
                .userLoginId(model.getUserLoginId())
                .fullname(model.getFullname())
                .status(model.getStatus())
                .email(model.getEmail())
                .build();
    }
}
