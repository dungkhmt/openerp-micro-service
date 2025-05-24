package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data.PageWrapperExampleUseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
