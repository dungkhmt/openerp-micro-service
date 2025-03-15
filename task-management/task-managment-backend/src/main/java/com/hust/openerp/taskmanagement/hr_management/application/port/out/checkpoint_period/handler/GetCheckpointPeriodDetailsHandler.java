package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.usecase_data.GetCheckpointPeriodDetails;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigureDetails;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodDetailsModel;

import java.util.List;
import java.util.UUID;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointPeriodDetailsHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<CheckpointPeriodDetailsModel, GetCheckpointPeriodDetails>
{
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(GetCheckpointPeriodDetails.class,this);
    }

    @Override
    public CheckpointPeriodDetailsModel handle(GetCheckpointPeriodDetails useCase) {
        var simpleModel = checkpointPeriodPort.findByCode(useCase.getCheckpointPeriodId());
        var configures = getConfigures(useCase.getCheckpointPeriodId());
        return CheckpointPeriodDetailsModel.of(simpleModel, configures);
    }

    private List<CheckpointPeriodConfigureDetailsModel> getConfigures(UUID checkpointPeriodId) {
        var useCase = new GetCheckpointPeriodConfigureDetails(checkpointPeriodId);
        return publishCollection(CheckpointPeriodConfigureDetailsModel.class, useCase).stream().toList();
    }
}
