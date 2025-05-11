package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.BasePageableRequest;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.GetDepartment;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;

import java.io.Serializable;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetDepartmentRequest extends BasePageableRequest {
    private String departmentCode;
    private String departmentName;
    private DepartmentStatus status;

    public GetDepartment toUseCase(){
        return GetDepartment.builder()
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .status(status)
                .pageableRequest(this)
                .build();
    }

    @Override
    public String getSortBy() {
        return "departmentCode";
    }

    @Override
    public SortDirection getOrder() {
        return SortDirection.DESC;
    }
}
