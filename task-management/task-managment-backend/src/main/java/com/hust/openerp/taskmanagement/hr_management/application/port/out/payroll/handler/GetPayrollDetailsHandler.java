package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IPayrollDetailPort;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.GetPayrollDetails;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PayrollDetailModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPayrollDetailsHandler extends ObservableUseCasePublisher
    implements CollectionUseCaseHandler<PayrollDetailModel, GetPayrollDetails> {
    private final IPayrollDetailPort payrollDetailPort;

    @Override
    public void init() {
        register(GetPayrollDetails.class, this);
    }

    @Override
    public Collection<PayrollDetailModel> handle(GetPayrollDetails useCase) {
        return payrollDetailPort.getDetails(useCase.getPayrollId());
    }
}
