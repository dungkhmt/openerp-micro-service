package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IRosterTemplatePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.DeleteRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class DeleteRosterTemplateHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<DeleteRosterTemplate> {

    private final IRosterTemplatePort rosterTemplatePort;

    @Override
    public void init() {
        register(DeleteRosterTemplate.class, this);
    }

    @Override
    @Transactional
    public void handle(DeleteRosterTemplate useCase) {
        try {
            rosterTemplatePort.cancelRosterTemplate(useCase.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApplicationException(
                    ResponseCode.EXCEPTION_ERROR,
                    "delete rosterTemplate error");
        }
    }
}
