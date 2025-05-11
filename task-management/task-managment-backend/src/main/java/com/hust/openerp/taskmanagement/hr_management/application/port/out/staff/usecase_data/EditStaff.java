package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;

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

    public static EditStaff delete(String staffCode) {
        return EditStaff.builder()
            .staffCode(staffCode)
            .staffStatus(StaffStatus.INACTIVE)
            .build();
    }

    public StaffModel toModel(){
        return StaffModel.builder()
                .fullname(fullName)
                .userLoginId(userLoginId)
                .staffCode(staffCode)
                .status(staffStatus)
                .build();
    }
}
