package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetPayroll implements UseCase {
    private UUID payrollId;
}
