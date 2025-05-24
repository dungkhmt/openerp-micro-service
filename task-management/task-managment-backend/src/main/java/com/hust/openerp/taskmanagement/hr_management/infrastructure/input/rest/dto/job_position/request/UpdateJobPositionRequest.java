package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.UpdateJobPosition;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateJobPositionRequest {
    private String name;
    private String description;
    private JobPositionType type;

    public UpdateJobPosition toUseCase(String code){
        return UpdateJobPosition.builder()
            .code(code)
            .name(name)
            .type(type)
            .description(description)
            .build();
    }
}
