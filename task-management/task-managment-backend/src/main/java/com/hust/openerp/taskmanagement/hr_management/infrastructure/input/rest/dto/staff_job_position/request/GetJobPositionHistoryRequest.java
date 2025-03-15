package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.staff_job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.StaffJobPositionHistory;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetJobPositionHistoryRequest {
    @NotNull
    private String userLoginId;

    public StaffJobPositionHistory toUseCase(){
        return StaffJobPositionHistory.builder()
                .userLoginId(userLoginId)
                .build();
    }
}
