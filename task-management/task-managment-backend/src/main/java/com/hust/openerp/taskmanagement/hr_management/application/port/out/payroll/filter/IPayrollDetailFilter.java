package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.filter;

import java.util.List;
import java.util.UUID;

public interface IPayrollDetailFilter {
    List<String> getUserLoginIds();
    UUID getPayrollId();
}
