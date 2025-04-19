package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsenceList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAbsenceListHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<AbsenceModel, GetAbsenceList>
{
    private final IAbsencePort absencePort;

    @Override
    public void init() {
        register(GetAbsenceList.class,this);
    }

    @Override
    public Collection<AbsenceModel> handle(GetAbsenceList useCase) {
        return absencePort.getAbsences(useCase.getUserIds(), useCase.getStartDate(), useCase.getEndDate());
    }
}
