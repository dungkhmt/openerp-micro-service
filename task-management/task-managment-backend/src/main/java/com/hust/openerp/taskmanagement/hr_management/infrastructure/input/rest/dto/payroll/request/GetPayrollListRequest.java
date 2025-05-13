package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollList;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetPayrollListRequest extends PageableRequest {
    private String searchName;

    public GetPayrollList toUseCase(){
        return GetPayrollList.builder()
            .searchName(searchName)
            .pageableRequest(this)
            .build();
    }
}
