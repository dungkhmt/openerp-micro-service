package openerp.openerpresourceserver.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateCheckpointPeriodHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateCheckpointPeriod>
{
    private final ICheckpointPeriodPort departmentPort;

    @Override
    public void init() {
        register(UpdateCheckpointPeriod.class,this);
    }

    @Override
    public void handle(UpdateCheckpointPeriod useCase) {
        var model = useCase.toModel();
        departmentPort.updateCheckpointPeriod(model);
    }
}
