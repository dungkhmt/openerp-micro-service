package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class AssignJobPosition implements UseCase {
    private String userLoginId;
    private String jobPositionCode;
}
