package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class GetAllCheckpointPeriod implements ICheckpointPeriodFilter, UseCase {
    private String name;
    private CheckpointPeriodStatus status;
    private IPageableRequest pageableRequest;
}
