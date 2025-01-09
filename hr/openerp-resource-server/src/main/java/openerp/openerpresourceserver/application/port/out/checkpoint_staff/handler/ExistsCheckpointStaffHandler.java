package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.ExistsCheckpointStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExistsCheckpointStaffHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<Boolean, ExistsCheckpointStaff>
{
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(ExistsCheckpointStaff.class,this);
    }

    @Override
    public Boolean handle(ExistsCheckpointStaff useCase) {
        return checkpointStaffPort.existCheckpointStaff(useCase.getPeriodId());
    }
}
