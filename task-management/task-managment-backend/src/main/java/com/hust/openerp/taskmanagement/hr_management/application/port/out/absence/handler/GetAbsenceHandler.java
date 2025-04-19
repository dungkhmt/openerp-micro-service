package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsence;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAbsenceHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<AbsenceModel, GetAbsence>
{
    private final IAbsencePort absencePort;

    @Override
    public void init() {
        register(GetAbsence.class,this);
    }

    @Override
    public AbsenceModel handle(GetAbsence useCase) {
        return absencePort.getAbsence(useCase.getId());
    }
}
