package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollDetailPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.CreatePayrollDetails;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CreatePayrollDetailsHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<PayrollDetailModel, CreatePayrollDetails> {
    private final IPayrollDetailPort payrollDetailPort;

    @Override
    public void init() {
        register(CreatePayrollDetails.class, this);
    }

    @Override
    public Collection<PayrollDetailModel> handle(CreatePayrollDetails useCase) {
        return payrollDetailPort.createDetails(useCase.getPayrollDetails());
    }
}
