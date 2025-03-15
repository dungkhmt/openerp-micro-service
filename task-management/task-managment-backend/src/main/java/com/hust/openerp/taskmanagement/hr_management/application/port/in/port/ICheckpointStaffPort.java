package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffModel;

import java.util.List;
import java.util.UUID;

public interface ICheckpointStaffPort {

    List<CheckpointStaffModel> getAllCheckpointStaff(ICheckpointStaffFilter filter);

    void checkpointStaff(List<CheckpointStaffModel> checkpointStaffModels);

    List<CheckpointStaffDetailsModel> getCheckpointStaffDetails(UUID periodId, String userLoginId);

    List<CheckpointStaffDetailsModel> getCheckpointStaffDetailsIn(UUID periodId, List<String> userLoginIds);

    Boolean existCheckpointStaff(UUID periodId);
}
