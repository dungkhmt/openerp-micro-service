package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_period.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.GetAllCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.BasePageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAllCheckpointPeriodRequest extends BasePageableRequest {
    private String name;
    private CheckpointPeriodStatus status;

    public GetAllCheckpointPeriod toUseCase(){
        return GetAllCheckpointPeriod.builder()
                .name(name)
                .status(status)
                .pageableRequest(this)
                .build();
    }

    @Override
    public String getSortBy() {
        return "checkpointDate";
    }

    @Override
    public SortDirection getOrder() {
        return SortDirection.DESC;
    }
}
