package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;

public interface IJobPositionFilter {
    String getCode();
    String getName();
    JobPositionStatus getStatus();
    JobPositionType getType();
}
