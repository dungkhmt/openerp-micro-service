package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.CreatePayroll;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollList;
import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreatePayrollRequest {
    private String name;
    private Integer totalWorkDays;
    private Float workHoursPerDay;
    private Integer totalHolidayDays;
    private LocalDate fromdate;
    private LocalDate thruDate;
    private String createdBy;
    private PayrollStatus status;

    public CreatePayroll toUseCase(){
        return CreatePayroll.builder()
            .name(name)
            .totalWorkDays(totalWorkDays)
            .workHoursPerDay(workHoursPerDay)
            .totalHolidayDays(totalHolidayDays)
            .fromdate(fromdate)
            .thruDate(thruDate)
            .createdBy(createdBy)
            .status(status)
            .build();
    }
}
