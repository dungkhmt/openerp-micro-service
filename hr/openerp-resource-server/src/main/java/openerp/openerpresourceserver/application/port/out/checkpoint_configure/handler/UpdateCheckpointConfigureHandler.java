package openerp.openerpresourceserver.application.port.out.checkpoint_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.UpdateCheckpointConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateCheckpointConfigureHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateCheckpointConfigure>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(UpdateCheckpointConfigure.class,this);
    }

    @Override
    public void handle(UpdateCheckpointConfigure useCase) {
        var model = useCase.toModel();
        checkpointConfigurePort.updateCheckpointConfigure(model);
    }
}
