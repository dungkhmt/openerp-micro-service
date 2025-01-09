package openerp.openerpresourceserver.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.GetAllCheckpointPeriod;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCheckpointPeriodHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<CheckpointPeriodModel, GetAllCheckpointPeriod>
{
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(GetAllCheckpointPeriod.class,this);
    }

    @Override
    public PageWrapper<CheckpointPeriodModel> handle(GetAllCheckpointPeriod useCase) {
        return checkpointPeriodPort.getCheckpointPeriod(useCase, useCase.getPageableRequest());
    }
}
