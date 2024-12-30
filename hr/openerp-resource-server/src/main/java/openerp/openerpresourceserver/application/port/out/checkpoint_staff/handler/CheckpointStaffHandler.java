package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.CheckpointStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CheckpointStaffHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<CheckpointStaff>
{
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(CheckpointStaff.class,this);
    }

    @Override
    @Transactional
    public void handle(CheckpointStaff useCase) {
        useCase.getModels().forEach(
                model -> {
                    model.setCheckedByUserId(useCase.getUserId());
                    model.setPeriodId(useCase.getPeriodId());
                    model.setCheckedByUserId(useCase.getUserId());
                }
        );
        checkpointStaffPort.checkpointStaff(useCase.getModels());
    }
}
