package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.UpdateJobPosition;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
