package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.job_position.usecase_data.UpdateJobPosition;
import openerp.openerpresourceserver.constant.JobPositionStatus;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DeleteJobPositionRequest {
    @NotNull
    private String code;

    public UpdateJobPosition toUseCase(){
        return UpdateJobPosition.builder()
                .code(code)
                .status(JobPositionStatus.INACTIVE)
                .build();
    }
}
