package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;

public interface IPayrollFilter {
    String getSearchName();
    PayrollStatus getStatus();
}
