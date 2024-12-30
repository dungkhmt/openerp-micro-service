package openerp.openerpresourceserver.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.CreateCheckpointPeriod;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class CreateCheckpointPeriodHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<CreateCheckpointPeriod> {
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(CreateCheckpointPeriod.class,this);
    }

    @Override
    public void handle(CreateCheckpointPeriod useCase) {
        checkpointPeriodPort.createCheckpointPeriod( useCase.toModel());
    }
}
