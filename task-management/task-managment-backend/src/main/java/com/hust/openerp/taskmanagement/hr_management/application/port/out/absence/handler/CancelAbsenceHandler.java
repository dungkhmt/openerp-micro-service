package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.CancelAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateAbsenceLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CancelAbsenceHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<CancelAbsence>
{
    private final IAbsencePort absencePort;

    @Override
    public void init() {
        register(CancelAbsence.class,this);
    }

    @Override
    public void handle(CancelAbsence useCase) {
        var absence = absencePort.getAbsence(useCase.getId());
        if(!absence.getUserId().equals(useCase.getUserId())){
            throw new ApplicationException(ResponseCode.UNAUTHORIZED, "User not authorized");
        }
        absencePort.cancelAbsence(useCase.getId());
        publish(
            UpdateAbsenceLeaveHours.of(
                absence.getDurationTimeAbsence(),
                0f,
                absence.getUserId()
            )
        );
    }
}
