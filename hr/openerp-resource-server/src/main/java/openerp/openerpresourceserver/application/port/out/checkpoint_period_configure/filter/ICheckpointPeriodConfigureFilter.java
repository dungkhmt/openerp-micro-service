package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter;

import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;

public interface ICheckpointPeriodConfigureFilter {
    String getPeriodId();
    CheckpointPeriodConfigureStatus getStatus();
}
