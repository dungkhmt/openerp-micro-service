package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.AddStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddStaffRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String fullname;

    private String userLoginId;
    private String email;

    private StaffStatus staffStatus;
    private String departmentCode;
    private String jobPositionCode;

    public AddStaff toUseCase(){
        return AddStaff.builder()
                .fullName(fullname)
                .userLoginId(userLoginId)
                .email(email)
                .staffStatus(staffStatus)
                .departmentCode(departmentCode)
                .jobPositionCode(jobPositionCode)
                .build();
    }
}
