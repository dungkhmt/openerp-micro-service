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
public class UpdateDepartment implements UseCase {
    private String departmentCode;
    private String departmentName;
    private String description;
    private DepartmentStatus status;

    public static UpdateDepartment delete(String departmentCode) {
        return UpdateDepartment.builder()
            .departmentCode(departmentCode)
            .status(DepartmentStatus.INACTIVE)
            .build();
    }

    public DepartmentModel toModel() {
        return DepartmentModel.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .description(description)
                .status(status)
                .build();
    }
}
