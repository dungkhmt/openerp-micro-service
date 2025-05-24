package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IRosterTemplatePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.UpdateRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateRosterTemplateHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateRosterTemplate> {
    private final IRosterTemplatePort rosterTemplatePort;

    @Override
    public void init() {
        register(UpdateRosterTemplate.class, this);
    }

    @Override
    @Transactional
    public void handle(UpdateRosterTemplate useCase) {
        rosterTemplatePort.updateRosterTemplate(useCase.getRosterTemplateModel());
    }
}
