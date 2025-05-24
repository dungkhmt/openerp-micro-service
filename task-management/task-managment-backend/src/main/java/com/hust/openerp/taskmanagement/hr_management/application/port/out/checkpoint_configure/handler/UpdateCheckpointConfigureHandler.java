package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.UpdateCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateCheckpointConfigureHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateCheckpointConfigure>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(UpdateCheckpointConfigure.class,this);
    }

    @Override
    public void handle(UpdateCheckpointConfigure useCase) {
        var model = useCase.toModel();
        checkpointConfigurePort.updateCheckpointConfigure(model);
    }
}
