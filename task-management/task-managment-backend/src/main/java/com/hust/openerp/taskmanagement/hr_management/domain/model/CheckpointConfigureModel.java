package com.hust.openerp.taskmanagement.hr_management.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;

@Getter
@Setter
@Builder
public class CheckpointConfigureModel {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;
}
