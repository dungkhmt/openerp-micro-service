package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCurrentStaffJobPosition implements UseCase {
    private List<String> userLoginIds;
}
