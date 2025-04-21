package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.service.AbsenceValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.AnnounceAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateAbsenceLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
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
    private final IConfigPort configPort;

    @Override
    public void init() {
        register(AnnounceAbsence.class,this);
    }

    @Override
    public void handle(AnnounceAbsence useCase) {
        var model = useCase.toModel();
        var companyConfig = configPort.getCompanyConfig();
        absenceValidator.validate(model);
        if(useCase.getType() == AbsenceType.PAID_LEAVE){
            absenceValidator.validateLeaveHour(useCase.getUserId(), model.getDurationTimeAbsence(companyConfig));
        }
        var absence = absencePort.createAbsence(useCase.toModel());
        publish(
            UpdateAbsenceLeaveHours.of(
                0f,
                absence.getDurationTimeAbsence(companyConfig),
                absence.getUserId()
            )
        );
    }
}
