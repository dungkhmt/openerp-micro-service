package openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;

@Data
@Builder
@Getter
@Setter
public class CreateCheckpointConfigure implements UseCase {
    private String name;
    private String description;
    private CheckpointConfigureStatus status;

    public CheckpointConfigureModel toModel() {
        return CheckpointConfigureModel.builder()
                .name(name)
                .description(description)
                .status(status == null? CheckpointConfigureStatus.ACTIVE : status)
                .build();
    }
}
