package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.GetAllCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCheckpointConfigureHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<CheckpointConfigureModel, GetAllCheckpointConfigure>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(GetAllCheckpointConfigure.class,this);
    }

    @Override
    public PageWrapper<CheckpointConfigureModel> handle(GetAllCheckpointConfigure useCase) {
        return checkpointConfigurePort.getCheckpointConfigure(useCase, useCase.getPageableRequest());
    }
}
