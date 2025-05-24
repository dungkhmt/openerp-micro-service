package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.GetCheckpointPeriodDetails;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigureDetails;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureDetailsModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodDetailsModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
