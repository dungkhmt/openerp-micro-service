package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.List;
import java.util.UUID;

public interface ICheckpointPeriodPort {
    CheckpointPeriodModel findByCode(UUID code);
    List<CheckpointPeriodModel> findByCodeIn(List<UUID> ids);
    CheckpointPeriodModel createCheckpointPeriod(CheckpointPeriodModel checkpointPeriod);
    void updateCheckpointPeriod(CheckpointPeriodModel checkpointPeriod);
    PageWrapper<CheckpointPeriodModel> getCheckpointPeriod(ICheckpointPeriodFilter filter, IPageableRequest request);
}
