package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCurrentDepartment implements UseCase {
    private String userLoginId;
}
