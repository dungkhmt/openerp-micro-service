package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.projection;

import java.math.BigDecimal;

public interface CheckpointPeriodConfigureProjection {
    String getCheckpointCode();
    String getName();
    String getDescription();
    String getStatus();
    String getConfigureStatus();
    BigDecimal getCoefficient();
}
