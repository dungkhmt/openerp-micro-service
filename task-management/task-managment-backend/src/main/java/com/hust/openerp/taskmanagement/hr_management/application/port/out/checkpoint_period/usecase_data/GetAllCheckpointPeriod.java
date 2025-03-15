package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class GetAllCheckpointPeriod implements ICheckpointPeriodFilter, UseCase {
    private String name;
    private CheckpointPeriodStatus status;
    private IPageableRequest pageableRequest;
}
