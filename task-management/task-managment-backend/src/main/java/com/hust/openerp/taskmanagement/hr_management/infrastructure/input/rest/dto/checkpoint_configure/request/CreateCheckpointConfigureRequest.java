package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.CreateCheckpointConfigure;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateCheckpointConfigureRequest {
    @NotNull
    private String name;
    private String description;

    public CreateCheckpointConfigure toUseCase(){
        return CreateCheckpointConfigure.builder()
                .name(name)
                .description(description)
                .build();
    }
}
