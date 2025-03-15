package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigure;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigureDetails;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetPeriodConfigureDetailsHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointPeriodConfigureDetailsModel, GetCheckpointPeriodConfigureDetails>
{
    private final ICheckpointPeriodConfigurePort periodConfigurePort;

    @Override
    public void init() {
        register(GetCheckpointPeriodConfigureDetails.class,this);
    }

    @Override
    public Collection<CheckpointPeriodConfigureDetailsModel> handle(GetCheckpointPeriodConfigureDetails useCase) {
        return periodConfigurePort.getPeriodConfigureDetails(useCase.getPeriodId());
    }
}
