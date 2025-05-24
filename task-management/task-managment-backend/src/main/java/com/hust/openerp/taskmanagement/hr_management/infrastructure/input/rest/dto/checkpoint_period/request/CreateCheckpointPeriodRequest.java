package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.CreateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCheckpointPeriodRequest {
    @NotNull
    private String name;
    private String description;
    @NotNull
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;
    private List<CreateCheckpointPeriodConfigureRequest> configures;

    public CreateCheckpointPeriod toUseCase(){
        return CreateCheckpointPeriod.builder()
                .name(name)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .description(description)
                .configures(CreateCheckpointPeriodConfigureRequest.toModels(configures))
                .build();
    }
}
