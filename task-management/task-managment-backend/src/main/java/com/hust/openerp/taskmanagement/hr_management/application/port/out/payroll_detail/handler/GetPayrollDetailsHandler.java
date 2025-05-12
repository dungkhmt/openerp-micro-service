package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll_detail.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll_detail.usecase_data.GetPayrollDetails;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPayrollDetailsHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<PayrollDetailModel, GetPayrollDetails> {

    private final IPayrollPort payrollPort;

    @Override
    public void init() {
        register(GetPayrollDetails.class, this);
    }

    @Override
    public PayrollDetailModel handle(GetPayrollDetails useCase) {
        return payrollPort.getPayrollDetails(useCase.getPayrollId());
    }
}
