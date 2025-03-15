package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;

import java.util.List;

public interface IStaffDepartmentPort {
    void assignDepartment(String userLoginId, String departmentCode);
    StaffDepartmentModel findCurrentDepartment(String userLoginId);
    List<StaffDepartmentModel> findCurrentDepartmentIn(List<String> userLoginIds);
    List<StaffDepartmentModel> findDepartmentHistory(String userLoginId);
}
