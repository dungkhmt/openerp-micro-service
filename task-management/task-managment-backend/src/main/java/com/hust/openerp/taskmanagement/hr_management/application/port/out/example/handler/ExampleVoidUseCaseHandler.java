package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data.VoidExampleUseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleVoidUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<VoidExampleUseCase> {

    @Override
    public void init() {
        register(VoidExampleUseCase.class,this);
    }

    @Override
    public void handle(VoidExampleUseCase useCase) {

    }
}
