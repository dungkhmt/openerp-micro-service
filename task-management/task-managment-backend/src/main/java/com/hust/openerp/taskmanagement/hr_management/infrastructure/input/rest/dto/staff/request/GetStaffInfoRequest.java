package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetAllStaffInfo;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetStaffInfo;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetStaffInfoRequest {
    private String userLoginId;
    private String staffCode;

    public GetStaffInfo toUseCase(){
        return GetStaffInfo.builder()
                .userLoginId(userLoginId)
                .staffCode(staffCode)
                .build();
    }
}
