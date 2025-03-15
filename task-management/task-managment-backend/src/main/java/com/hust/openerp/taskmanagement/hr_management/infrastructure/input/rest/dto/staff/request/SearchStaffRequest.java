package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchStaffRequest {
    private String fullname;
    private String staffCode;
    private String staffEmail;
    private StaffStatus status;
    private String departmentCode;
    private String jobPositionCode;
    private PageableRequest pageableRequest;

    public FindStaff toUseCase(){
        return FindStaff.builder()
                .staffEmail(staffEmail)
                .staffCode(staffCode)
                .staffName(fullname)
                .status(status)
                .departmentCode(departmentCode)
                .jobPositionCode(jobPositionCode)
                .pageableRequest(pageableRequest)
                .build();
    }
}
