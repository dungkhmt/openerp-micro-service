package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.GetDepartment;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetDepartmentRequest {
    private String departmentCode;
    private String departmentName;
    private DepartmentStatus status;
    private PageableRequest pageableRequest;

    public GetDepartment toUseCase(){
        return GetDepartment.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .status(status)
                .pageableRequest(pageableRequest)
                .build();
    }
}
