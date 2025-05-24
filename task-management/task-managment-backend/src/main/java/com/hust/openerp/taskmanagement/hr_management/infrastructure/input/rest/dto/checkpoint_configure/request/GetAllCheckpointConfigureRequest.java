package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.GetAllCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.BasePageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAllCheckpointConfigureRequest extends BasePageableRequest {
    private String name;
    private CheckpointConfigureStatus status;

    public GetAllCheckpointConfigure toUseCase(){
        return GetAllCheckpointConfigure.builder()
                .name(name)
                .status(status)
                .pageableRequest(this)
                .build();
    }

    @Override
    public String getSortBy() {
        return "checkpointCode";
    }

    @Override
    public SortDirection getOrder() {
        return SortDirection.DESC;
    }
}
