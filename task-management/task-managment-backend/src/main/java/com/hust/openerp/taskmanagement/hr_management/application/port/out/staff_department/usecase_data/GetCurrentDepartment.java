package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCurrentDepartment implements UseCase {
    private String userLoginId;
}
