package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPayrollListHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<PayrollModel, GetPayrollList> {
    private final IPayrollPort payrollPort;

    @Override
    public void init() {
        register(GetPayrollList.class, this);
    }

    @Override
    public PageWrapper<PayrollModel> handle(GetPayrollList useCase) {
        return payrollPort.getPayrolls(useCase, useCase.getPageableRequest());
    }
}
