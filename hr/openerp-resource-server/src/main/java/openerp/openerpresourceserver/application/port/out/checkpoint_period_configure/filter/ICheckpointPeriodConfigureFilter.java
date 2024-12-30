package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter;

import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

public interface ICheckpointPeriodConfigureFilter {
    String getPeriodId();
    CheckpointPeriodStatus getStatus();
}
