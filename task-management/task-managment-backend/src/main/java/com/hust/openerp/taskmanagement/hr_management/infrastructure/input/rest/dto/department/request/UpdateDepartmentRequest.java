package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.UpdateDepartment;
import openerp.openerpresourceserver.constant.DepartmentStatus;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateDepartmentRequest {
    @NotNull
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public UpdateDepartment toUseCase(){
        return UpdateDepartment.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .description(description)
                .status(status)
                .build();
    }
}
