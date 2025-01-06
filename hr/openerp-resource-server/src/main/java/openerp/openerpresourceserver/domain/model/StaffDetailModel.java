package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.StaffStatus;

@Getter
@Setter
@Builder
public class StaffDetailModel {
    private String staffCode;
    private String userLoginId;
    private String fullname;
    private StaffStatus status;
    private String email;
    private DepartmentModel department;
    private JobPositionModel jobPosition;

    public static StaffDetailModel of(
            StaffModel staffModel,
            DepartmentModel departmentModel,
            JobPositionModel jobPositionModel
    ){
        return StaffDetailModel.builder()
                .staffCode(staffModel.getStaffCode())
                .userLoginId(staffModel.getUserLoginId())
                .fullname(staffModel.getFullname())
                .status(staffModel.getStatus())
                .email(staffModel.getEmail())
                .department(departmentModel)
                .jobPosition(jobPositionModel)
                .build();
    }
}
