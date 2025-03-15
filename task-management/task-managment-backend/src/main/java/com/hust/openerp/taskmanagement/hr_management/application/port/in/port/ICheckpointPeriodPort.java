package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.List;
import java.util.UUID;

public interface ICheckpointPeriodPort {
    CheckpointPeriodModel findByCode(UUID code);
    List<CheckpointPeriodModel> findByCodeIn(List<UUID> ids);
    CheckpointPeriodModel createCheckpointPeriod(CheckpointPeriodModel checkpointPeriod);
    void updateCheckpointPeriod(CheckpointPeriodModel checkpointPeriod);
    PageWrapper<CheckpointPeriodModel> getCheckpointPeriod(ICheckpointPeriodFilter filter, IPageableRequest request);
}
