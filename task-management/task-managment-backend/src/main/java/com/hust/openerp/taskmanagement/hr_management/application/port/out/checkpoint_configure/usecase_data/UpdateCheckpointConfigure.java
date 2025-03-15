package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class UpdateCheckpointConfigure implements UseCase {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;

    public CheckpointConfigureModel toModel() {
        return CheckpointConfigureModel.builder()
                .code(code)
                .name(name)
                .description(description)
                .status(status)
                .build();
    }
}
