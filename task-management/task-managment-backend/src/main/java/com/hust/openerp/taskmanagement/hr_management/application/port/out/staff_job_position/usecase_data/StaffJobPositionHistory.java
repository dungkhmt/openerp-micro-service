package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
public class StaffJobPositionHistory implements UseCase {
    private String userLoginId;
}
