package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.DeletePayroll;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request.CreatePayrollRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request.GetPayrollDetailRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.request.GetPayrollListRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.response.PayrollDetailResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.payroll.response.PayrollResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payrolls")
@RequiredArgsConstructor
public class PayrollController extends BeanAwareUseCasePublisher {

    @PostMapping
    public ResponseEntity<?> createPayroll(
        @Valid @RequestBody CreatePayrollRequest request
    ) {
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayroll(@PathVariable UUID id) {
        publish(new DeletePayroll(id));
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @GetMapping("/{payrollId}/details")
    public ResponseEntity<?> getPayrollDetails(
        @PathVariable UUID payrollId,
        @Valid @ModelAttribute GetPayrollDetailRequest params
        )
    {
        var modelPage = publishPageWrapper(PayrollDetailModel.class, params.toUseCase(payrollId));
        var responsePage = modelPage.convert(PayrollDetailResponse::fromModel);
        return ResponseEntity.ok().body(
            new Resource(responsePage)
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getPayrolls(
        @Valid @ModelAttribute GetPayrollListRequest params
    )
    {
        var modelPage = publishPageWrapper(PayrollModel.class, params.toUseCase());
        var responsePage = modelPage.convert(PayrollResponse::fromModel);
        return ResponseEntity.ok().body(
            new Resource(responsePage)
        );
    }
}
