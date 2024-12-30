package openerp.openerpresourceserver.application.port.out.checkpoint_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.GetCheckpointConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointConfigureHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<CheckpointConfigureModel, GetCheckpointConfigure>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(GetCheckpointConfigure.class,this);
    }

    @Override
    public PageWrapper<CheckpointConfigureModel> handle(GetCheckpointConfigure useCase) {
        return checkpointConfigurePort.getCheckpointConfigure(useCase, useCase.getPageableRequest());
    }
}
