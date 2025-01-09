package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaffDetails;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointStaffDetailsModel;

import java.util.Collection;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetCheckpointStaffDetailsHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffDetailsModel, GetCheckpointStaffDetails> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetCheckpointStaffDetails.class,this);
    }

    @Override
    public Collection<CheckpointStaffDetailsModel> handle(GetCheckpointStaffDetails useCase) {
        return checkpointStaffPort.getCheckpointStaffDetails(useCase.getPeriodId(), useCase.getUserId());
    }
}
