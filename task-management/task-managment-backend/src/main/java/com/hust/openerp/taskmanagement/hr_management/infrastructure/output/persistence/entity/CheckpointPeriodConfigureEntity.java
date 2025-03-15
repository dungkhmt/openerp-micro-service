package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_period_configure")
public class CheckpointPeriodConfigureEntity extends AuditEntity{
    @EmbeddedId
    private CheckpointPeriodConfigureId id;

    @Column(name = "coefficient")
    private BigDecimal coefficient;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private CheckpointPeriodConfigureStatus status;
}