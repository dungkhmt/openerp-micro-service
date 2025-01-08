package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_period.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointPeriodResponse {
    private UUID id;
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;

    public static CheckpointPeriodResponse fromModel(CheckpointPeriodModel model) {
        return CheckpointPeriodResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .checkpointDate(model.getCheckpointDate())
                .createdByUserId(model.getCreatedByUserId())
                .status(model.getStatus())
                .build();
    }
}
