package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.CreatePayroll;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreatePayrollRequest {
    @NotNull
    private String name;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate thruDate;

    public CreatePayroll toUseCase(String createdBy){
        return CreatePayroll.builder()
            .name(name)
            .fromDate(fromDate)
            .thruDate(thruDate)
            .createdBy(createdBy)
            .build();
    }
}
