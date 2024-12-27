package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.StaffStatus;

@Getter
@Setter
@Builder
public class StaffModel {
    private String staffCode;
    private String userLoginId;
    private String fullname;
    private StaffStatus status;
    private String email;
}
