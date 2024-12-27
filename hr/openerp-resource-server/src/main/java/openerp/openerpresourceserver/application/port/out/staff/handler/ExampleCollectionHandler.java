package openerp.openerpresourceserver.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.example.usecase_data.IterableUseCaseExample;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.ExampleModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleCollectionHandler extends ObservableUseCasePublisher implements CollectionUseCaseHandler<ExampleModel, IterableUseCaseExample> {

    @Override
    public void init() {
        register(IterableUseCaseExample.class, this);
    }

    @Override
    public Collection<ExampleModel> handle(IterableUseCaseExample useCase) {
        return null;
    }
}
