package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IShiftPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.GetShiftList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetShiftListHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<ShiftModel, GetShiftList>
{
    private final IShiftPort shiftPort;

    @Override
    public void init() {
        register(GetShiftList.class,this);
    }

    @Override
    public Collection<ShiftModel> handle(GetShiftList useCase) {
        return shiftPort.getShifts(
            useCase.getUserIds(), useCase.getStartDate(), useCase.getEndDate(), useCase.isHasUnassigned()
        );
    }
}
