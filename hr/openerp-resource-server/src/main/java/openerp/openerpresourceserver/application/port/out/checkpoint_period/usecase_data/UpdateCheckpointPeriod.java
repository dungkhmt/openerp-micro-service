package openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class UpdateCheckpointPeriod implements UseCase {
    private UUID id;
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;

    public CheckpointPeriodModel toModel() {
        return CheckpointPeriodModel.builder()
                .id(id)
                .name(name)
                .description(description)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .build();
    }
}
