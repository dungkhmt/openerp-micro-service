package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data;

import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCurrentStaffDepartment implements UseCase {
    private List<String> userLoginIds;
}
