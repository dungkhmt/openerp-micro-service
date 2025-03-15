package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.filter;

import openerp.openerpresourceserver.constant.StaffStatus;

public interface IStaffFilter {
    String getStaffCode();
    String getStaffName();
    StaffStatus getStatus();
    String getStaffEmail();
    String getDepartmentCode();
    String getJobPositionCode();
}
