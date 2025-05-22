package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IShiftPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.DeleteShifts;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class DeleteShiftsHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<DeleteShifts>
{
    private final IShiftPort shiftPort;

    @Override
    public void init() {
        register(DeleteShifts.class,this);
    }

    @Override
    public void handle(DeleteShifts useCase) {
        shiftPort.deleteShifts(useCase.getIds());
    }
}
