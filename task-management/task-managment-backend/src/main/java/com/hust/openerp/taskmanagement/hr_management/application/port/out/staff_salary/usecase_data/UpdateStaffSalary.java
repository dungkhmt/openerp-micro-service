package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class UpdateStaffSalary implements UseCase {
    @NotNull
    private String userLoginId;
    @NotNull
    private Integer salary;
    private SalaryType salaryType;

    public StaffSalaryModel toModel(){
        return StaffSalaryModel.builder()
                .userLoginId(getUserLoginId())
                .salary(salary)
                .fromDate(LocalDateTime.now())
                .salaryType(salaryType == null ? SalaryType.getDefaultValue() : salaryType)
                .build();
    }
}
