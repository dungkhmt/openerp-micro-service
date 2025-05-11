package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.job_position.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.BasePageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.GetJobPosition;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetJobPositionRequest extends BasePageableRequest {
    private String code;
    private String name;
    private JobPositionStatus status;

    public GetJobPosition toUseCase(){
        return GetJobPosition.builder()
                .code(code)
                .name(name)
                .status(status)
                .pageableRequest(this)
                .build();
    }

    @Override
    public String getSortBy() {
        return "positionCode";
    }

    @Override
    public SortDirection getOrder() {
        return SortDirection.DESC;
    }
}
