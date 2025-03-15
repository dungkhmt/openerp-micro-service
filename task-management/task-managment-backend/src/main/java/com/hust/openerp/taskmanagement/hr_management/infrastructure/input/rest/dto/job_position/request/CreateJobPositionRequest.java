package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.CreateJobPosition;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateJobPositionRequest {
    @NotNull
    private String name;
    private String description;

    public CreateJobPosition toUseCase(){
        return CreateJobPosition.builder()
                .name(name)
                .description(description)
                .build();
    }
}
