package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaffDetailsIn;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointStaffDetailsModel;

import java.util.Collection;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetCheckpointStaffDetailsInHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffDetailsModel, GetCheckpointStaffDetailsIn> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetCheckpointStaffDetailsIn.class,this);
    }

    @Override
    public Collection<CheckpointStaffDetailsModel> handle(GetCheckpointStaffDetailsIn useCase) {
        return checkpointStaffPort.getCheckpointStaffDetailsIn(useCase.getPeriodId(), useCase.getUserIds());
    }
}
