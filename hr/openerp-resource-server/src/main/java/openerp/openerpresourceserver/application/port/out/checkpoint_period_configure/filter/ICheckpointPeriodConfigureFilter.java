package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter;

import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;

import java.util.UUID;

public interface ICheckpointPeriodConfigureFilter {
    UUID getPeriodId();
    CheckpointPeriodConfigureStatus getStatus();
}
