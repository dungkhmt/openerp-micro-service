package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;

public interface IDepartmentFilter {
    String getDepartmentCode();
    String getDepartmentName();
    DepartmentStatus getStatus();
}
