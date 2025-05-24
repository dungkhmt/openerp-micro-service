package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CreatePayrollDetails implements UseCase {
    private List<PayrollDetailModel> payrollDetails;
}
