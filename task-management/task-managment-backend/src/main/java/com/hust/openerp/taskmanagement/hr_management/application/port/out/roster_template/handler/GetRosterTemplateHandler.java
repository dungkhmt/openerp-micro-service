package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IRosterTemplatePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.GetRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetRosterTemplateHandler extends ObservableUseCasePublisher
    implements UseCaseHandler<RosterTemplateModel, GetRosterTemplate> {
    private final IRosterTemplatePort rosterTemplatePort;

    @Override
    public void init() {
        register(GetRosterTemplate.class, this);
    }

    @Override
    public RosterTemplateModel handle(GetRosterTemplate useCase) {
        return rosterTemplatePort.getRosterTemplate(useCase.getId());
    }
}
