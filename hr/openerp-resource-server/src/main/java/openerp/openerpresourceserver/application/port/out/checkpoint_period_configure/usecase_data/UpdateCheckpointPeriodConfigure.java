package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class UpdateCheckpointPeriodConfigure implements UseCase {
    private UUID periodId;
    List<CheckpointPeriodConfigureModel> models;
}
