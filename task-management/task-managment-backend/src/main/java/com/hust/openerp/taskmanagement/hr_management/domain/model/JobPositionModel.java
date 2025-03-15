package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.JobPositionStatus;

@Getter
@Setter
@Builder
public class JobPositionModel {
    private String code;
    private String name;
    private String description;
    private JobPositionStatus status;
}
