package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;

@Data
@Builder
@Getter
@Setter
public class UpdateDepartment implements UseCase {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public DepartmentModel toModel() {
        return DepartmentModel.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .description(description)
                .status(status)
                .build();
    }
}
