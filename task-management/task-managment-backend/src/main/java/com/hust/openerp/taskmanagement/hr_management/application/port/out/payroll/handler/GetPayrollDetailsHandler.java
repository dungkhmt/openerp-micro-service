package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollDetailPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollDetails;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPayrollDetailsHandler extends ObservableUseCasePublisher
    implements PageWrapperUseCaseHandler<PayrollDetailModel, GetPayrollDetails> {
    private final IPayrollDetailPort payrollDetailPort;

    @Override
    public void init() {
        register(GetPayrollDetails.class, this);
    }

    @Override
    public PageWrapper<PayrollDetailModel> handle(GetPayrollDetails useCase) {
        return payrollDetailPort.getDetails(useCase, useCase.getPageableRequest());
    }
}
