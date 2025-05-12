package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.CreatePayroll;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CreatePayrollHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<CreatePayroll> {

    private final IPayrollPort payrollPort;

    @Override
    public void init() {
        register(CreatePayroll.class, this);
    }

    @Override
    @Transactional
    public void handle(CreatePayroll useCase) {
        try {
            payrollPort.createPayroll(useCase.getPayrollModel());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApplicationException(
                    ResponseCode.EXCEPTION_ERROR,
                    "create payroll error");
        }
    }
}
