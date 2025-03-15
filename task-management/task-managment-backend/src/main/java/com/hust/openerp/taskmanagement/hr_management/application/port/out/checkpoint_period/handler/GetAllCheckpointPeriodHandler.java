package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period.usecase_data.GetAllCheckpointPeriod;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointPeriodModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCheckpointPeriodHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<CheckpointPeriodModel, GetAllCheckpointPeriod>
{
    private final ICheckpointPeriodPort checkpointPeriodPort;

    @Override
    public void init() {
        register(GetAllCheckpointPeriod.class,this);
    }

    @Override
    public PageWrapper<CheckpointPeriodModel> handle(GetAllCheckpointPeriod useCase) {
        return checkpointPeriodPort.getCheckpointPeriod(useCase, useCase.getPageableRequest());
    }
}
