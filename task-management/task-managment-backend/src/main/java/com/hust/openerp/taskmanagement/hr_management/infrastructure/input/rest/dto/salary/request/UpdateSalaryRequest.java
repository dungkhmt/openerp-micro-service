package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.UpdateStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateSalaryRequest {
    @Min(value = 0)
    private Integer salary;
    private SalaryType salaryType;

    public UpdateStaffSalary toUseCase(String userLoginId){
        return UpdateStaffSalary.builder()
                .userLoginId(userLoginId)
                .salary(salary)
                .salaryType(salaryType)
                .build();
    }
}
