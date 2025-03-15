package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodConfigureStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointPeriodConfigureDetailsModel {
    private CheckpointConfigureModel configureModel;
    private UUID checkpointPeriodId;
    private BigDecimal coefficient;
    private CheckpointPeriodConfigureStatus status;

    public CheckpointPeriodConfigureDetailsModel of(
            CheckpointConfigureModel configureModel,
            CheckpointPeriodConfigureModel periodConfigureModel
    ) {
        return CheckpointPeriodConfigureDetailsModel.builder()
                .configureModel(configureModel)
                .checkpointPeriodId(periodConfigureModel.getCheckpointPeriodId())
                .coefficient(periodConfigureModel.getCoefficient())
                .status(periodConfigureModel.getStatus())
                .build();
    }
}
