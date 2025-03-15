package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPeriodConfigureHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointPeriodConfigureModel, GetCheckpointPeriodConfigure>
{
    private final ICheckpointPeriodConfigurePort periodConfigurePort;

    @Override
    public void init() {
        register(GetCheckpointPeriodConfigure.class,this);
    }

    @Override
    public Collection<CheckpointPeriodConfigureModel> handle(GetCheckpointPeriodConfigure useCase) {
        return periodConfigurePort.getAllPeriodConfigure(useCase);
    }
}
