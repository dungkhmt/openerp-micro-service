package openerp.openerpresourceserver.application.port.out.example.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.example.usecase_data.ExampleUseCase;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExampleVoidUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<ExampleUseCase> {

    @Override
    public void init() {
        register(ExampleUseCase.class,this);
    }

    @Override
    public void handle(ExampleUseCase useCase) {

    }
}
