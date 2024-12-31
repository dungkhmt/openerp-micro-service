package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;

@Data
@Builder
@Getter
@Setter
public class GetCheckpointPeriodConfigure implements ICheckpointPeriodConfigureFilter, UseCase {
    private String periodId;
    private CheckpointPeriodConfigureStatus status;
}
