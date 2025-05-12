package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll_detail.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetPayrollDetails implements UseCase {
    private UUID payrollId;
}
