package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayroll;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPayrollHandler extends ObservableUseCasePublisher
    implements UseCaseHandler<PayrollModel, GetPayroll> {
    private final IPayrollPort payrollPort;

    @Override
    public void init() {
        register(GetPayroll.class, this);
    }

    @Override
    public PayrollModel handle(GetPayroll useCase) {
        return payrollPort.getPayroll(useCase.getPayrollId());
    }
}
