package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointPeriodConfigureModel {
    private String configureId;
    private UUID checkpointPeriodId;
    private BigDecimal coefficient;
    private CheckpointPeriodConfigureStatus status;
}
