package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class DeletePayroll {
    private UUID payrollId;
}
