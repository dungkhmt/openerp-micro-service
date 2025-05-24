package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CreateCheckpointConfigure implements UseCase {
    private String name;
    private String description;
    private CheckpointConfigureStatus status;

    public CheckpointConfigureModel toModel() {
        return CheckpointConfigureModel.builder()
                .name(name)
                .description(description)
                .status(status == null? CheckpointConfigureStatus.ACTIVE : status)
                .build();
    }
}
