package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.filter;

import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;

public interface ICheckpointConfigureFilter {
    String getName();
    CheckpointConfigureStatus getStatus();
}
