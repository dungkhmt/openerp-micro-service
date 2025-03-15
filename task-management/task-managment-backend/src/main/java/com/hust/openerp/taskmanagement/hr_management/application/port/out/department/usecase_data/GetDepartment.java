package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.department.filter.IDepartmentFilter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

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
