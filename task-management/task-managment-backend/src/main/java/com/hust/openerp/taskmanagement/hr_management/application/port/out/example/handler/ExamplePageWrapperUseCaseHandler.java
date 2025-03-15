package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.example.usecase_data.PageWrapperExampleUseCase;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.ExampleModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExamplePageWrapperUseCaseHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<ExampleModel,PageWrapperExampleUseCase>
{

    @Override
    public void init() {
        register(PageWrapperExampleUseCase.class,this);
    }

    @Override
    public PageWrapper<ExampleModel> handle(PageWrapperExampleUseCase useCase) {
        return null;
    }
}
