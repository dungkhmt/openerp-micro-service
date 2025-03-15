package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data;

import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;

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
