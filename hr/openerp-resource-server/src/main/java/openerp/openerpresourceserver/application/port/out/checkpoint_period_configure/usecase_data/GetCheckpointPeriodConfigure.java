package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCheckpointPeriodConfigure implements ICheckpointPeriodConfigureFilter, UseCase {
    private UUID periodId;
    private CheckpointPeriodConfigureStatus status;
}
