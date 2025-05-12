package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;

@Getter
@Setter
@Builder
public class CreatePayroll {
    private PayrollModel payrollModel;
}
