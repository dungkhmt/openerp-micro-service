package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointConfigureStaffResponse {
    private String configureId;
    private BigDecimal point;

    public static CheckpointConfigureStaffResponse fromModel(CheckpointStaffModel model) {
        return CheckpointConfigureStaffResponse.builder()
                .configureId(model.getConfigureId())
                .point(model.getPoint())
                .build();
    }

    public static List<CheckpointConfigureStaffResponse> fromModels(
            List<CheckpointStaffModel> models
    ) {
        if(models == null) return null;
        return models.stream()
                .map(CheckpointConfigureStaffResponse::fromModel)
                .toList();
    }
}
