package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCheckpointPeriodRequest {
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;
    private List<CreateCheckpointPeriodConfigureRequest> configures;


    public UpdateCheckpointPeriod toUseCase(UUID id){
        return UpdateCheckpointPeriod.builder()
                .id(id)
                .name(name)
                .description(description)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .configures(CreateCheckpointPeriodConfigureRequest.toModels(configures))
                .build();
    }


}
