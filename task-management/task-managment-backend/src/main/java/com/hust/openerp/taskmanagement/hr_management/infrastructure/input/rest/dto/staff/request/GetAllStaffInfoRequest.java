package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.GetAllStaffInfo;
import openerp.openerpresourceserver.constant.StaffStatus;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAllStaffInfoRequest {
    private String fullname;
    private String staffCode;
    private String staffEmail;
    private StaffStatus status;
    private PageableRequest pageableRequest;
    private String jobPositionCode;
    private String departmentCode;

    public GetAllStaffInfo toUseCase(){
        return GetAllStaffInfo.builder()
                .staffEmail(staffEmail)
                .staffCode(staffCode)
                .staffName(fullname)
                .status(status)
                .jobPositionCode(jobPositionCode)
                .departmentCode(departmentCode)
                .pageableRequest(pageableRequest)
                .build();
    }
}
