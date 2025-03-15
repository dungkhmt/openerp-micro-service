package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;

import java.util.List;
import java.util.UUID;

public interface ICheckpointPeriodConfigurePort {
    List<CheckpointPeriodConfigureDetailsModel> getPeriodConfigureDetails(UUID checkpointPeriodId);
    List<CheckpointPeriodConfigureModel> getAllPeriodConfigure(ICheckpointPeriodConfigureFilter filter);
    void deleteAllPeriodConfigure(UUID periodId);
    void createPeriodConfigure(List<CheckpointPeriodConfigureModel> models);
}
