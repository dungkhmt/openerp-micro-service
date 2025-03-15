package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class CreateJobPosition implements UseCase {
    private String name;
    private String description;

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
                .name(name)
                .description(description)
                .status(JobPositionStatus.ACTIVE)
                .build();
    }
}
