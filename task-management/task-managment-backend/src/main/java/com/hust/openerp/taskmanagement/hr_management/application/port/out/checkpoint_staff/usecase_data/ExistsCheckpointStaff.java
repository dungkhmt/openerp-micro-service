package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExistsCheckpointStaff implements UseCase {
    private UUID periodId;
}
