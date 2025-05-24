package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeleteCheckpointPeriodRequest {
    @NotNull
    private UUID id;

    public UpdateCheckpointPeriod toUseCase(){
        return UpdateCheckpointPeriod.builder()
            .id(id)
            .status(CheckpointPeriodStatus.INACTIVE)
            .build();
    }
}
