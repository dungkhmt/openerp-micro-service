package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {
    private final IPayrollPort payrollPort;

    @PostMapping
    public PayrollModel createPayroll(@RequestBody PayrollModel payrollModel) {
        return payrollPort.createPayroll(payrollModel);
    }

    @DeleteMapping("/{id}")
    public void cancelPayroll(@PathVariable UUID id) {
        payrollPort.cancelPayroll(id);
    }

    @GetMapping("/{id}")
    public PayrollModel getPayroll(@PathVariable UUID id) {
        return payrollPort.getPayroll(id);
    }

    @GetMapping
    public List<PayrollModel> getPayrolls(@RequestParam List<String> userIds,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return payrollPort.getPayrolls(userIds, startDate, endDate);
    }

    @GetMapping("/{id}/details")
    public PayrollModel getPayrollDetails(@PathVariable UUID id) {
        return payrollPort.getPayrollDetails(id);
    }

    @GetMapping("/configure-details")
    public Map<String, Object> getPayrollConfigureDetails() {
        return payrollPort.getPayrollConfigureDetails();
    }
}
