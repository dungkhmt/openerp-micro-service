package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointPeriodDetailsResponse extends CheckpointPeriodResponse {
    List<CheckpointPeriodConfigureResponse> configures;

    public static CheckpointPeriodDetailsResponse fromModel(CheckpointPeriodDetailsModel model) {
        return CheckpointPeriodDetailsResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .checkpointDate(model.getCheckpointDate())
                .createdByUserId(model.getCreatedByUserId())
                .status(model.getStatus())
                .configures(CheckpointPeriodConfigureResponse.fromModels(model.getConfigures()))
                .build();
    }
}
