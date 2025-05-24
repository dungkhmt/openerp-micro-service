package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.CreateRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CreateRosterTemplateHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<CreateRosterTemplate> {

    @Override
    public void init() {
        register(CreateRosterTemplate.class, this);
    }

    @Override
    @Transactional
    public void handle(CreateRosterTemplate useCase) {

    }
}
