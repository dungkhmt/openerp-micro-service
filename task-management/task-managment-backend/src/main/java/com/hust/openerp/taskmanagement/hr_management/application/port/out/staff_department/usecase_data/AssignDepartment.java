package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AssignDepartment implements UseCase {
    private String userLoginId;
    private String departmentCode;
}
