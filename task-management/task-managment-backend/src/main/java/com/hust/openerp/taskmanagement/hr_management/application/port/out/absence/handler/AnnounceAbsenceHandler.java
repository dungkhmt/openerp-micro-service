package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.service.AbsenceValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.AnnounceAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateAbsenceLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class AnnounceAbsenceHandler extends ObservableUseCasePublisher
    implements VoidUseCaseHandler<AnnounceAbsence>
{
    private final IAbsencePort absencePort;
    private final AbsenceValidator absenceValidator;

    @Override
    public void init() {
        register(AnnounceAbsence.class,this);
    }

    @Override
    public void handle(AnnounceAbsence useCase) {
        var model = useCase.toModel();
        absenceValidator.validate(model);
        var absence = absencePort.createAbsence(useCase.toModel());
        publish(
            UpdateAbsenceLeaveHours.of(
                0f,
                absence.getDurationTimeAbsence(),
                absence.getUserId()
            )
        );
    }
}
