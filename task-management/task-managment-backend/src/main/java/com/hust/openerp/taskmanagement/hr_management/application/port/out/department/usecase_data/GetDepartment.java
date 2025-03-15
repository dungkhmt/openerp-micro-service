package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.filter.IDepartmentFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class GetDepartment implements IDepartmentFilter, UseCase {
    private String departmentCode;
    private String departmentName;
    private DepartmentStatus status;
    private IPageableRequest pageableRequest;
}
