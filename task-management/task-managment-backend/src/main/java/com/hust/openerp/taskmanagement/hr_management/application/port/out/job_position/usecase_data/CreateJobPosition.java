package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CreateJobPosition implements UseCase {
    private String name;
    private String description;
    private JobPositionType type;

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
            .name(name)
            .description(description)
            .type(type == null ? JobPositionType.FULL_TIME : type)
            .status(JobPositionStatus.ACTIVE)
            .build();
    }
}
