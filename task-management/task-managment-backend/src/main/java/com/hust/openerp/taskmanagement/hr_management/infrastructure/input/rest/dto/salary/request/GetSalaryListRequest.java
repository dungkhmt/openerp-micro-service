package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetAllCurrentStaffSalary;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetSalaryListRequest {
    private List<String> userIds;

    public GetAllCurrentStaffSalary toUseCase(){
        return new GetAllCurrentStaffSalary(userIds);
    }
}
