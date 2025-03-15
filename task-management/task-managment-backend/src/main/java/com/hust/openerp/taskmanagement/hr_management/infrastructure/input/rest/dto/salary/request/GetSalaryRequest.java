package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetCheckinout;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetCurrentStaffSalary;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetSalaryRequest {
    @NotNull
    private String userLoginId;

    public GetCurrentStaffSalary toUseCase(){
        return GetCurrentStaffSalary.builder()
                .userLoginId(userLoginId)
                .build();
    }
}
