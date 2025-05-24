package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IRosterTemplatePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.GetRosterTemplateList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetRosterTemplateListHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<RosterTemplateModel, GetRosterTemplateList> {
    private final IRosterTemplatePort rosterTemplatePort;

    @Override
    public void init() {
        register(GetRosterTemplateList.class, this);
    }

    @Override
    public Collection<RosterTemplateModel> handle(GetRosterTemplateList useCase) {
        return rosterTemplatePort.getRosterTemplates();
    }
}
