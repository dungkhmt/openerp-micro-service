package openerp.openerpresourceserver.application.port.out.checkpoint_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.GetAllCheckpointConfigure;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.GetCheckpointConfigureIn;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointConfigureInHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointConfigureModel, GetCheckpointConfigureIn>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(GetCheckpointConfigureIn.class,this);
    }

    @Override
    public Collection<CheckpointConfigureModel> handle(GetCheckpointConfigureIn useCase) {
        return checkpointConfigurePort.findByCodeIn(useCase.getConfigureCodes());
    }
}
