package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;

public interface ICheckpointPeriodFilter {
    String getName();
    CheckpointPeriodStatus getStatus();
}
