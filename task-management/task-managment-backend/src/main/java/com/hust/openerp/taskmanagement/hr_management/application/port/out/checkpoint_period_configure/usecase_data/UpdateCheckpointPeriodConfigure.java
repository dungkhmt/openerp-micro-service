package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class UpdateCheckpointPeriodConfigure implements UseCase {
    private UUID periodId;
    List<CheckpointPeriodConfigureModel> configures;
}
