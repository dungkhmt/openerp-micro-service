package openerp.openerpresourceserver.application.port.out.example.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.example.usecase_data.IterableUseCaseExample;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.IterableUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleIterableHandler extends ObservableUseCasePublisher implements IterableUseCaseHandler<ExampleModel, IterableUseCaseExample> {

    @Override
    public void init() {
        register(IterableUseCaseExample.class, this);
    }

    @Override
    public Iterable<ExampleModel> handle(IterableUseCaseExample useCase) {
        return null;
    }
}
