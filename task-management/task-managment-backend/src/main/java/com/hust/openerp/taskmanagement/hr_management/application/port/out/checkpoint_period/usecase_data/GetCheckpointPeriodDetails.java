package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCheckpointPeriodDetails implements UseCase {
    UUID checkpointPeriodId;
}
