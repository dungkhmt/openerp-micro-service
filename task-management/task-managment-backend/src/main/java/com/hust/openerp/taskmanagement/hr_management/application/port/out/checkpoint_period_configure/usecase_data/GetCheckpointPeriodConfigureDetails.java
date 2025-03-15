package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data;

import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCheckpointPeriodConfigureDetails implements UseCase {
    private UUID periodId;
}
