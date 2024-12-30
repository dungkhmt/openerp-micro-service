package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.DeleteAllCheckpointPeriodConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.Collection;
import java.util.List;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetAllCheckpointStaffOfPeriodHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffModel,DeleteAllCheckpointPeriodConfigure> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(DeleteAllCheckpointPeriodConfigure.class,this);
    }

    @Override
    public Collection<CheckpointStaffModel> handle(DeleteAllCheckpointPeriodConfigure useCase) {
        return List.of();
    }
}
