package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.EditStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeleteStaffRequest {
    private String staffCode;
    private String userLoginId;

    public EditStaff toUseCase(){
        return EditStaff.builder()
                .userLoginId(userLoginId)
                .staffCode(staffCode)
                .staffStatus(StaffStatus.INACTIVE)
                .build();
    }
}
