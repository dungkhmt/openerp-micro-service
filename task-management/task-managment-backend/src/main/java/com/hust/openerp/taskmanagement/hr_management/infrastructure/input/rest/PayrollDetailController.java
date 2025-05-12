package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollDetailPort;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payroll-details")
@RequiredArgsConstructor
public class PayrollDetailController {
    private final IPayrollDetailPort payrollDetailPort;

    @GetMapping("/{payrollId}")
    public List<PayrollDetailModel> getDetailsByPayrollId(@PathVariable UUID payrollId) {
        return payrollDetailPort.getDetailsByPayrollId(payrollId);
    }

    @PostMapping
    public PayrollDetailModel saveDetail(@RequestBody PayrollDetailModel model) {
        return payrollDetailPort.saveDetail(model);
    }
}
