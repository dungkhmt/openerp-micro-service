package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CreatePayroll implements UseCase {
    private String name;
    private LocalDate fromdate;
    private LocalDate thruDate;
    private String createdBy;
}
