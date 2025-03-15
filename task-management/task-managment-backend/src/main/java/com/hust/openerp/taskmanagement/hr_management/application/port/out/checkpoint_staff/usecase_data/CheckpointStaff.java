package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffModel;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class CheckpointStaff implements UseCase {
    private UUID periodId;
    private String userId;
    private String checkedByUserId;
    List<CheckpointStaffModel> models;
}
