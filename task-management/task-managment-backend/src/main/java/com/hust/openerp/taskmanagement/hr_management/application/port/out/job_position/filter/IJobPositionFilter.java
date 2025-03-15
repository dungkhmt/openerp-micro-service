package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;

public interface IJobPositionFilter {
    String getCode();
    String getName();
    JobPositionStatus getStatus();
}
