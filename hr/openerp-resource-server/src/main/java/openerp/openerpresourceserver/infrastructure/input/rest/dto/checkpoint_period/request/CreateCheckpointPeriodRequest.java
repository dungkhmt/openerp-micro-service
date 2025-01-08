package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.CreateCheckpointPeriod;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCheckpointPeriodRequest {
    @NotNull
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;

    public CreateCheckpointPeriod toUseCase(){
        return CreateCheckpointPeriod.builder()
                .name(name)
                .checkpointDate(checkpointDate)
                .createdByUserId(createdByUserId)
                .status(status)
                .description(description)
                .build();
    }
}
