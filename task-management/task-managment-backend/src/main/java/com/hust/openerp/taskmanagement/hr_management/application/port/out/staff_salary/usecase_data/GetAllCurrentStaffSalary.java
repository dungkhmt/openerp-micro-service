package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class GetAllCurrentStaffSalary implements UseCase {
    private List<String> userLoginIds;
}
