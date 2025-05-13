package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter.IPayrollDetailFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;

import java.util.List;
import java.util.UUID;

public interface IPayrollDetailPort {
    List<PayrollDetailModel> getDetails(UUID payrollId);
    PageWrapper<PayrollDetailModel> getDetails(UUID payrollId, IPayrollDetailFilter filter, IPageableRequest pageableRequest);
    List<PayrollDetailModel> createDetails(List<PayrollDetailModel> payrollDetailModel);
    PayrollDetailModel saveDetail(PayrollDetailModel model);
}
