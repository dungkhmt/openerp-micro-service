package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.JobPositionStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class UpdateJobPosition implements UseCase {
    private String code;
    private String name;
    private String description;
    private JobPositionStatus status;

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
                .code(code)
                .name(name)
                .description(description)
                .status(status)
                .build();
    }
}
