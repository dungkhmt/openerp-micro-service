package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.GetAllCheckpointPeriod;
import openerp.openerpresourceserver.constant.CheckpointPeriodStatus;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAllCheckpointPeriodRequest {
    private String name;
    private CheckpointPeriodStatus status;
    private PageableRequest pageableRequest;

    public GetAllCheckpointPeriod toUseCase(){
        return GetAllCheckpointPeriod.builder()
                .name(name)
                .status(status)
                .pageableRequest(pageableRequest)
                .build();
    }
}
