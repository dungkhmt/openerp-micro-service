package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodConfigureStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
