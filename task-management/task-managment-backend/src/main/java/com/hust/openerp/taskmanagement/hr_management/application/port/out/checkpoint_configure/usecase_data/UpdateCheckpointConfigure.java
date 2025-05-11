package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class UpdateCheckpointConfigure implements UseCase {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;

    public static UpdateCheckpointConfigure delete(String code) {
        return UpdateCheckpointConfigure.builder()
            .code(code)
            .status(CheckpointConfigureStatus.INACTIVE)
            .build();
    }

    public CheckpointConfigureModel toModel() {
        return CheckpointConfigureModel.builder()
                .code(code)
                .name(name)
                .description(description)
                .status(status)
                .build();
    }
}
