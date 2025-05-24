package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffModel;
import lombok.*;

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
