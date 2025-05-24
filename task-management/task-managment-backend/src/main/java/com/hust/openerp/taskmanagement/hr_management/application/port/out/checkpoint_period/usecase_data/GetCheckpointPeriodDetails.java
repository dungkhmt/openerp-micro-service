package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCheckpointPeriodDetails implements UseCase {
    UUID checkpointPeriodId;
}
