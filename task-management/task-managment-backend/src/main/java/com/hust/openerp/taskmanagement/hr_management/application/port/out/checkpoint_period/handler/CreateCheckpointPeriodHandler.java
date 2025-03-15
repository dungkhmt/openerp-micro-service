package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.CreateCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.UpdateCheckpointPeriodConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;

import java.util.List;
import java.util.UUID;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class CreateCheckpointPeriodHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<CreateCheckpointPeriod> {
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(CreateCheckpointPeriod.class,this);
    }

    @Override
    public void handle(CreateCheckpointPeriod useCase) {
        var savedModel = checkpointPeriodPort.createCheckpointPeriod( useCase.toModel());
        var configures = useCase.getConfigures();
        if(configures == null || configures.isEmpty()) return;
        createPeriodConfigure(savedModel.getId(), configures);
    }

    private void createPeriodConfigure(UUID periodId, List<CheckpointPeriodConfigureModel> configures) {
        var useCase = UpdateCheckpointPeriodConfigure.builder()
                .periodId(periodId)
                .configures(configures)
                .build();
        publish(useCase);
    }
}
