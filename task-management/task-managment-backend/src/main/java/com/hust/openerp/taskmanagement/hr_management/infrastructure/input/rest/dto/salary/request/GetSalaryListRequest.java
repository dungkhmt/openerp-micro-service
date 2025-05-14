package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.GetDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetAllCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.BasePageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetSalaryListRequest extends BasePageableRequest {
    private List<String> userIds;

    public GetAllCurrentStaffSalary toUseCase(){
        return new GetAllCurrentStaffSalary(userIds);
    }

    @Override
    public String getSortBy() {
        return "userId";
    }

    @Override
    public SortDirection getOrder() {
        return SortDirection.DESC;
    }
}
