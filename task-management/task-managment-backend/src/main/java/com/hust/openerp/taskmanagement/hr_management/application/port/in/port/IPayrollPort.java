package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;

import java.util.UUID;

public interface IPayrollPort {
    PayrollModel createPayroll(PayrollModel payrollModel);
    void cancelPayroll(UUID id);
    PayrollModel getPayroll(UUID id);
    PageWrapper<PayrollModel> getPayrolls(IPayrollFilter filter, IPageableRequest pageableRequest);
}
