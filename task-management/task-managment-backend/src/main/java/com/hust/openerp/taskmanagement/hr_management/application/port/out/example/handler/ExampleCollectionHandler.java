package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data.IterableUseCaseExample;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleCollectionHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<ExampleModel, IterableUseCaseExample>
{
    @Override
    public void init() {
        register(IterableUseCaseExample.class, this);
    }

    @Override
    public Collection<ExampleModel> handle(IterableUseCaseExample useCase) {
        return null;
    }
}
