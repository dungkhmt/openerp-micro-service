package openerp.openerpresourceserver.application.port.out.department_detail.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.example.usecase_data.ExampleUseCase;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentJobPositionHandler extends ObservableUseCasePublisher implements UseCaseHandler<ExampleModel, ExampleUseCase> {

    @Override
    public void init() {
        register(ExampleUseCase.class,this);
    }

    @Override
    public ExampleModel handle(ExampleUseCase useCase) {
        return null;
    }
}
