package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

import java.util.List;

@Data
@Builder
@Getter
@Setter
public class GetAllCurrentStaffSalary implements UseCase {
    private List<String> userLoginIds;
}
