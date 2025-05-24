package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCheckpointPeriodConfigure implements ICheckpointPeriodConfigureFilter, UseCase {
    private UUID periodId;
    private CheckpointPeriodConfigureStatus status;
}
