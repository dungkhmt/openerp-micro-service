package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.CheckpointStaff;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CheckpointStaffRequest {
    @NotNull
    private UUID periodId;
    @NotNull
    private String userId;
    private String checkedByUserId;
    List<CheckpointConfigureStaffRequest> checkpointConfigures;

    public CheckpointStaff toUseCase(){
        return CheckpointStaff.builder()
                .periodId(periodId)
                .userId(userId)
                .checkedByUserId(checkedByUserId)
                .models(CheckpointConfigureStaffRequest.toModels(checkpointConfigures))
                .build();
    }
}
