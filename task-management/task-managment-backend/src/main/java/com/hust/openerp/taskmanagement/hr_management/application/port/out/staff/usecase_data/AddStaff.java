package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;

@Data
@Builder
@Getter
@Setter
@Slf4j
public class AddStaff implements UseCase {
    private String fullName;
    private String userLoginId;
    private String email;
    private StaffStatus staffStatus;
    private String departmentCode;
    private String jobPositionCode;

    public StaffModel toModel(){
        return StaffModel.builder()
                .fullname(fullName)
                .userLoginId(userLoginId)
                .email(email)
                .status(staffStatus == null ? StaffStatus.ACTIVE : staffStatus)
                .build();
    }
}
