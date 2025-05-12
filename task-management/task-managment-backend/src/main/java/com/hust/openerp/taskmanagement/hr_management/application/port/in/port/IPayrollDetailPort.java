package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;

import java.util.List;
import java.util.UUID;

public interface IPayrollDetailPort {
    List<PayrollDetailModel> getDetailsByPayrollId(UUID payrollId);

    PayrollDetailModel saveDetail(PayrollDetailModel model);
}
