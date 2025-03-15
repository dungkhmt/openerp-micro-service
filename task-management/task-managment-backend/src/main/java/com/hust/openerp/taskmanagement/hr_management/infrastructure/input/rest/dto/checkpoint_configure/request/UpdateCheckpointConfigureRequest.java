package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.UpdateCheckpointConfigure;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCheckpointConfigureRequest {
    private String code;
    private String name;
    private String description;


    public UpdateCheckpointConfigure toUseCase(){
        return UpdateCheckpointConfigure.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
    }
}
