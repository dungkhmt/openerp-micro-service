package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodDetailsModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointPeriodDetailsResponse extends CheckpointPeriodResponse {
    List<CheckpointPeriodConfigureResponse> configures;

    public static CheckpointPeriodDetailsResponse fromModel(CheckpointPeriodDetailsModel model) {
        return CheckpointPeriodDetailsResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .checkpointDate(model.getCheckpointDate())
                .createdByUserId(model.getCreatedByUserId())
                .status(model.getStatus())
                .configures(CheckpointPeriodConfigureResponse.fromModels(model.getConfigures()))
                .build();
    }
}
