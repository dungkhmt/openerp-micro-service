package openerp.openerpresourceserver.application.port.out.checkpoint_staff.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.CheckpointStaff;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpoint;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointModel;

import java.util.UUID;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CheckpointStaffHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<CheckpointModel,CheckpointStaff>
{
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(CheckpointStaff.class,this);
    }

    @Override
    @Transactional
    public CheckpointModel handle(CheckpointStaff useCase) {
        useCase.getModels().forEach(
                model -> {
                    model.setUserId(useCase.getUserId());
                    model.setPeriodId(useCase.getPeriodId());
                    model.setCheckedByUserId(useCase.getCheckedByUserId());
                }
        );
        checkpointStaffPort.checkpointStaff(useCase.getModels());
        return checkpoint(useCase.getUserId(), useCase.getPeriodId());
    }

    private CheckpointModel checkpoint(String userId, UUID periodId){
        var useCase = GetCheckpoint.builder()
                .userId(userId)
                .periodId(periodId)
                .build();
        return publish(CheckpointModel.class, useCase);
    }
}
