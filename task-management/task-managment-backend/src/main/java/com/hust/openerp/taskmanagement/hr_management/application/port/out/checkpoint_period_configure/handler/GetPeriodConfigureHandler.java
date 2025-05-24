package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.GetCheckpointPeriodConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodConfigureModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
