package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.UpdateDepartment;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeleteDepartmentRequest {
    @NotNull
    private String departmentCode;

    public UpdateDepartment toUseCase(){
        return UpdateDepartment.builder()
                .departmentCode(departmentCode)
                .status(DepartmentStatus.INACTIVE)
                .build();
    }
}
