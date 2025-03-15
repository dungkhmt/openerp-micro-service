package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data;

import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCurrentJobPosition implements UseCase {
    private String userLoginId;
}
