package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.CreateCheckpointPeriod;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.UpdateCheckpointPeriodConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;

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
