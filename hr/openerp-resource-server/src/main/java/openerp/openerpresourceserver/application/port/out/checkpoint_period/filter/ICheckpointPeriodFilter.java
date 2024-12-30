package openerp.openerpresourceserver.application.port.out.checkpoint_period.filter;

import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

public interface ICheckpointPeriodFilter {
    String getName();
    CheckpointPeriodStatus getStatus();
}
