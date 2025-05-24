package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.UpdateDepartment;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateDepartmentRequest {
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public UpdateDepartment toUseCase(String departmentCode){
        return UpdateDepartment.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .description(description)
                .status(status)
                .build();
    }
}
