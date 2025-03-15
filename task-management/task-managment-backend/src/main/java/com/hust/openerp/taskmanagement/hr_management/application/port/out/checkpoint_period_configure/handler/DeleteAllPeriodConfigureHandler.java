package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.DeleteAllCheckpointPeriodConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class DeleteAllPeriodConfigureHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<DeleteAllCheckpointPeriodConfigure> {
    private final ICheckpointPeriodConfigurePort checkpointPeriodConfigurePort;

    @Override
    public void init() {
        register(DeleteAllCheckpointPeriodConfigure.class,this);
    }

    @Override
    public void handle(DeleteAllCheckpointPeriodConfigure useCase) {
        checkpointPeriodConfigurePort.deleteAllPeriodConfigure(useCase.getPeriodId());
    }
}
