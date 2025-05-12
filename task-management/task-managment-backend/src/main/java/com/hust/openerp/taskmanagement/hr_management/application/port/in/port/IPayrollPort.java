package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPayrollPort {
    PayrollModel createPayroll(PayrollModel payrollModel);
    void cancelPayroll(UUID id);
    PayrollModel getPayroll(UUID id);
    List<PayrollModel> getPayrolls(List<String> userIds, LocalDate startDate, LocalDate endDate);
    PayrollModel getPayrollDetails(UUID payrollId);
    Map<String, Object> getPayrollConfigureDetails();
}
