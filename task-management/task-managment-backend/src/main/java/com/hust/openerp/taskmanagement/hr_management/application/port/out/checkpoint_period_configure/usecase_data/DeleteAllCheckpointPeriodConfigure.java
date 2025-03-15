package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class DeleteAllCheckpointPeriodConfigure implements UseCase {
    private UUID periodId;
}
