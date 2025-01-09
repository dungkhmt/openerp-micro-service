package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetAllCheckpointStaffs;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.Collection;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetAllCheckpointStaffsHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffModel, GetAllCheckpointStaffs> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetAllCheckpointStaffs.class,this);
    }

    @Override
    public Collection<CheckpointStaffModel> handle(GetAllCheckpointStaffs useCase) {
        return checkpointStaffPort.getAllCheckpointStaff(useCase);
    }
}
