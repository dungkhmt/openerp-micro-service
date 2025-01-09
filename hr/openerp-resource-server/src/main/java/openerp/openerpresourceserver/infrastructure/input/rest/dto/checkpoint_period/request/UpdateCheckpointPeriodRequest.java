package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCheckpointPeriodRequest {
    @NotNull
    private UUID id;
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;
    private List<CreateCheckpointPeriodConfigureRequest> configures;


    public UpdateCheckpointPeriod toUseCase(){
        return UpdateCheckpointPeriod.builder()
                .id(id)
                .name(name)
                .description(description)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .configures(CreateCheckpointPeriodConfigureRequest.toModels(configures))
                .build();
    }


}
