package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;

import java.util.List;

public interface IStaffJobPositionPort {
    void assignJobPosition(String userLoginId, String jobPositionCode);
    StaffJobPositionModel findCurrentJobPosition(String userLoginId);
    List<StaffJobPositionModel> findCurrentJobPositionIn(List<String> userLoginIds);
    List<StaffJobPositionModel> findJobPositionHistory(String userLoginId);
}
