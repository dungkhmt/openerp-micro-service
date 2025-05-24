package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.CreateDepartment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateDepartmentRequest {
    @NotNull
    private String departmentName;
    private String description;

    public CreateDepartment toUseCase(){
        return CreateDepartment.builder()
                .departmentName(departmentName)
                .description(description)
                .build();
    }
}
