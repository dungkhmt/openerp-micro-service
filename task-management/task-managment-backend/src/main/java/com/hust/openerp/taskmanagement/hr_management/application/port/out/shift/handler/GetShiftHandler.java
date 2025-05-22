package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IShiftPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.GetShift;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetShiftHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<ShiftModel, GetShift>
{
    private final IShiftPort shiftPort;

    @Override
    public void init() {
        register(GetShift.class,this);
    }

    @Override
    public ShiftModel handle(GetShift useCase) {
        return shiftPort.getShift(useCase.getId());
    }
}
