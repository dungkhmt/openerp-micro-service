package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;

@Data
@Builder
@Getter
@Setter
public class UpdateJobPosition implements UseCase {
    private String code;
    private String name;
    private String description;
    private JobPositionType type;
    private JobPositionStatus status;

    public static UpdateJobPosition delete(String code){
        return UpdateJobPosition.builder()
            .code(code)
            .status(JobPositionStatus.INACTIVE)
            .build();
    }

    public JobPositionModel toModel() {
        return JobPositionModel.builder()
            .code(code)
            .name(name)
            .description(description)
            .type(type)
            .status(status)
            .build();
    }
}
