package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.GetAllCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAllCheckpointConfigureRequest {
    private String name;
    private CheckpointConfigureStatus status;
    private PageableRequest pageableRequest;

    public GetAllCheckpointConfigure toUseCase(){
        return GetAllCheckpointConfigure.builder()
                .name(name)
                .status(status)
                .pageableRequest(pageableRequest)
                .build();
    }
}
