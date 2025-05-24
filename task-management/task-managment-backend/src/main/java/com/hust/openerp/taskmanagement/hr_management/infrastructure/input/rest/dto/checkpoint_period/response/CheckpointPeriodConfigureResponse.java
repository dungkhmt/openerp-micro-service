package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.response.CheckpointConfigureResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointPeriodConfigureResponse {
    private CheckpointConfigureResponse configure;
    private BigDecimal coefficient;
    private CheckpointPeriodConfigureStatus status;

    public static CheckpointPeriodConfigureResponse fromModel(
            CheckpointPeriodConfigureDetailsModel model
    ) {
        return CheckpointPeriodConfigureResponse.builder()
                .configure(CheckpointConfigureResponse.fromModel(model.getConfigureModel()))
                .coefficient(model.getCoefficient())
                .status(model.getStatus())
                .build();
    }

    public static List<CheckpointPeriodConfigureResponse> fromModels(
            List<CheckpointPeriodConfigureDetailsModel> models
    ) {
        if(models == null) return null;
        return models.stream()
                .map(CheckpointPeriodConfigureResponse::fromModel)
                .toList();
    }
}
