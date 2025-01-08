package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CheckpointPeriodDetailsModel {
    private UUID id;
    List<CheckpointPeriodConfigureDetailsModel> configures;
    private String name;
    private String description;
    private String checkpointDate;
    private String createdByUserId;
    private CheckpointPeriodStatus status;

    public CheckpointPeriodDetailsModel of(
        CheckpointPeriodModel model,
        List<CheckpointPeriodConfigureDetailsModel> configures
    ){
        return CheckpointPeriodDetailsModel.builder()
                .id(model.getId())
                .configures(configures)
                .name(model.getName())
                .description(model.getDescription())
                .checkpointDate(model.getCheckpointDate())
                .createdByUserId(model.getCreatedByUserId())
                .status(model.getStatus())
                .build();
    }
}
