package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;

import java.util.List;

public interface IStaffFilter {
    String getStaffCode();
    String getUserLoginId();
    String getStaffName();
    StaffStatus getStatus();
    String getStaffEmail();
    String getDepartmentCode();
    List<String> getDepartmentCodes();
    String getJobPositionCode();
    List<String> getJobPositionCodes();
}
