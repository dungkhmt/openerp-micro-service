package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCheckpointPeriodConfigureRequest {
    @NotNull
    private String configureId;
    @NotNull
    private BigDecimal coefficient;

    public CheckpointPeriodConfigureModel toModel(){
        return CheckpointPeriodConfigureModel.builder()
                .configureId(configureId)
                .coefficient(coefficient)
                .status(CheckpointPeriodConfigureStatus.ACTIVE)
                .build();
    }

    public static List<CheckpointPeriodConfigureModel> toModels(
            List<CreateCheckpointPeriodConfigureRequest> dtos
    ) {
        if(dtos == null) return null;
        return dtos.stream()
                .map(CreateCheckpointPeriodConfigureRequest::toModel)
                .toList();
    }
}
