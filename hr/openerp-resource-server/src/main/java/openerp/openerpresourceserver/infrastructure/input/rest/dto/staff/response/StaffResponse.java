package openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.domain.model.StaffModel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
