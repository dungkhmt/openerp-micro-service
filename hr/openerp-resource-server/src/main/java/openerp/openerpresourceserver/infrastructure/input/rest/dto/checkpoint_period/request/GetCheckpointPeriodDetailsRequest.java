package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.GetCheckpointPeriodDetails;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCheckpointPeriodDetailsRequest {
    @NotNull
    private UUID id;

    public GetCheckpointPeriodDetails toUseCase(){
        return new GetCheckpointPeriodDetails(id);
    }
}
