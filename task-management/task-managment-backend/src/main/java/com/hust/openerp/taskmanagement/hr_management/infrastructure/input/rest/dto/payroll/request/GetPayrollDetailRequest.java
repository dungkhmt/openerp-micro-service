package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollDetails;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollList;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetPayrollDetailRequest extends PageableRequest {
    private List<String> userId;

    public GetPayrollDetails toUseCase(UUID payrollId){
        return GetPayrollDetails.builder()
            .payrollId(payrollId)
            .userLoginIds(userId)
            .pageableRequest(this)
            .build();
    }
}
