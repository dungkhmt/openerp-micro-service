package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointStaffModel {
    private String userId;
    private UUID periodId;
    private String configureId;
    private BigDecimal point;
    private String checkedByUserId;
}
