package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointConfigureResponse {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;

    public static CheckpointConfigureResponse fromModel(CheckpointConfigureModel model) {
        return CheckpointConfigureResponse.builder()
                .code(model.getCode())
                .name(model.getName())
                .description(model.getDescription())
                .status(model.getStatus())
                .build();
    }
}
