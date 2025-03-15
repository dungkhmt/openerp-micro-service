package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.UpdateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.UpdateCheckpointPeriodConfigure;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.ExistsCheckpointStaff;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

import java.util.List;
import java.util.UUID;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateCheckpointPeriodHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateCheckpointPeriod>
{
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(UpdateCheckpointPeriod.class,this);
    }

    @Override
    public void handle(UpdateCheckpointPeriod useCase) {
        //validateUpdatePeriod(useCase.getId());
        var model = useCase.toModel();
        checkpointPeriodPort.updateCheckpointPeriod(model);
        var configures = useCase.getConfigures();
        if(configures == null || configures.isEmpty()) return;
        updatePeriodConfigure(useCase.getId(), configures);
    }

    private void validateUpdatePeriod(UUID periodId) {
        var useCase = new ExistsCheckpointStaff(periodId);
        if(publish(Boolean.class, useCase)){
            log.error("Cannot update the checkpoint period as it has already been evaluated.");
            throw new ApplicationException(
                    ResponseCode.CHECKPOINT_PERIOD_UPDATE_ERROR,
                    "Cannot update the checkpoint period as it has already been evaluated."
            );
        }
    }

    private void updatePeriodConfigure(UUID periodId, List<CheckpointPeriodConfigureModel> configures) {
        var useCase = UpdateCheckpointPeriodConfigure.builder()
                .periodId(periodId)
                .configures(configures)
                .build();
        publish(useCase);
    }
}
