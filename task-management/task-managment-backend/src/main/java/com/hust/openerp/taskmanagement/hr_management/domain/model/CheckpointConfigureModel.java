package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckpointConfigureModel {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;
}
