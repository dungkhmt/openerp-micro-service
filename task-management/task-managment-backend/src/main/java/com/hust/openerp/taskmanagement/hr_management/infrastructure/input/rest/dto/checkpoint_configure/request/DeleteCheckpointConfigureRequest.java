package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.UpdateCheckpointConfigure;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeleteCheckpointConfigureRequest {
    @NotNull
    private String code;

    public UpdateCheckpointConfigure toUseCase(){
        return UpdateCheckpointConfigure.builder()
                .code(code)
                .status(CheckpointConfigureStatus.INACTIVE)
                .build();
    }
}
