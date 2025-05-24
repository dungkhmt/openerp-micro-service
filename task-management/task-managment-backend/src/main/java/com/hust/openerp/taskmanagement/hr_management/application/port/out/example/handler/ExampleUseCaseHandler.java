package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data.ExampleUseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleUseCaseHandler extends ObservableUseCasePublisher implements UseCaseHandler<ExampleModel, ExampleUseCase> {

    @Override
    public void init() {
        register(ExampleUseCase.class,this);
    }

    @Override
    public ExampleModel handle(ExampleUseCase useCase) {
        return null;
    }
}
