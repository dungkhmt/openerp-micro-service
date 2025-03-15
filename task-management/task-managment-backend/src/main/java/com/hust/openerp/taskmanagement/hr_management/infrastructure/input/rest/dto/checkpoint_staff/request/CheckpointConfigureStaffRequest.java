package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointConfigureStaffRequest {
    @NotNull
    private String configureId;
    @NotNull
    private BigDecimal point;

    public CheckpointStaffModel toModel(){
        return CheckpointStaffModel.builder()
                .configureId(configureId)
                .point(point)
                .build();
    }

    public static List<CheckpointStaffModel> toModels(
            List<CheckpointConfigureStaffRequest> dtos
    ) {
        if(dtos == null) return null;
        return dtos.stream()
                .map(CheckpointConfigureStaffRequest::toModel)
                .toList();
    }
}
