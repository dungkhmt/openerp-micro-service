package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.response.CheckpointConfigureResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointPeriodConfigureResponse {
    private CheckpointConfigureResponse configure;
    private BigDecimal coefficient;
    private CheckpointPeriodConfigureStatus status;

    public static CheckpointPeriodConfigureResponse fromModel(
            CheckpointPeriodConfigureDetailsModel model
    ) {
        return CheckpointPeriodConfigureResponse.builder()
                .configure(CheckpointConfigureResponse.fromModel(model.getConfigureModel()))
                .coefficient(model.getCoefficient())
                .status(model.getStatus())
                .build();
    }

    public static List<CheckpointPeriodConfigureResponse> fromModels(
            List<CheckpointPeriodConfigureDetailsModel> models
    ) {
        if(models == null) return null;
        return models.stream()
                .map(CheckpointPeriodConfigureResponse::fromModel)
                .toList();
    }
}
