package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.UpdateJobPosition;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateJobPositionRequest {
    @NotNull
    private String code;
    private String name;
    private String description;
    private JobPositionType type;

    public UpdateJobPosition toUseCase(){
        return UpdateJobPosition.builder()
            .code(code)
            .name(name)
            .type(type)
            .description(description)
            .build();
    }
}
