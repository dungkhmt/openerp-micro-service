package com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateAbsenceLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateAbsenceLeaveHoursHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateAbsenceLeaveHours>
{
    private final IStaffPort staffPort;

    @Override
    public void init() {
        register(UpdateAbsenceLeaveHours.class,this);
    }

    @Override
    public void handle(UpdateAbsenceLeaveHours useCase) {
        staffPort.updateLeaveHours(useCase.getStaffFilter(), useCase.getLeaveHours(), useCase.getUpdateType());
    }
}
