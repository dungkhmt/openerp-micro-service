package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointStaffDetailsModel {
    private String userId;
    private UUID periodId;
    private CheckpointPeriodConfigureDetailsModel periodConfigure;
    private BigDecimal point;
    private String checkedByUserId;
}
