package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class CreateCheckpointPeriod implements UseCase {
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;
    private List<CheckpointPeriodConfigureModel> configures;

    public CheckpointPeriodModel toModel() {
        return CheckpointPeriodModel.builder()
                .name(name)
                .description(description)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status == null? CheckpointPeriodStatus.ACTIVE : status)
                .build();
    }
}
