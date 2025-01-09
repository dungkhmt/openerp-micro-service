package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import openerp.openerpresourceserver.domain.model.CheckpointModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointResponse {
    private UUID periodId;
    private String userId;
    List<CheckpointConfigureStaffResponse> configurePoints;
    private BigDecimal totalPoint;
    private String checkedByUserId;

    public static CheckpointResponse fromModel(CheckpointModel model) {
        return CheckpointResponse.builder()
                .periodId(model.getPeriodId())
                .userId(model.getUserId())
                .totalPoint(model.getTotalPoint())
                .checkedByUserId(model.getCheckedByUserId())
                .configurePoints(CheckpointConfigureStaffResponse.fromModels(model.getCheckpointStaffs()))
                .build();
    }
}
