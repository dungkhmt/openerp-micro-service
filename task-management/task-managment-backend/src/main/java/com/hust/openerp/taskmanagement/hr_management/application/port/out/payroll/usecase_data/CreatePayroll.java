package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CreatePayroll implements UseCase {
    private String name;
    private LocalDate fromDate;
    private LocalDate thruDate;
    private String createdBy;
}
