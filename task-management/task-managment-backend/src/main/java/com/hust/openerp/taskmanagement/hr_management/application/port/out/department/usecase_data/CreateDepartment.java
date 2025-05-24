package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CreateDepartment implements UseCase {
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public DepartmentModel toModel() {
        return DepartmentModel.builder()
                .departmentName(departmentName)
                .description(description)
                .status(status == null ? DepartmentStatus.ACTIVE : status)
                .build();
    }
}
