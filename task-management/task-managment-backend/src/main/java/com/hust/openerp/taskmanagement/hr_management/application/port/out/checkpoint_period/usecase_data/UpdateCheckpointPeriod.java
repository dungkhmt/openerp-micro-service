package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodModel;
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
public class UpdateCheckpointPeriod implements UseCase {
    private UUID id;
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;
    private List<CheckpointPeriodConfigureModel> configures;

    public static UpdateCheckpointPeriod delete(UUID id) {
        return UpdateCheckpointPeriod.builder()
            .id(id)
            .status(CheckpointPeriodStatus.INACTIVE)
            .build();
    }

    public CheckpointPeriodModel toModel() {
        return CheckpointPeriodModel.builder()
                .id(id)
                .name(name)
                .description(description)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .build();
    }
}
