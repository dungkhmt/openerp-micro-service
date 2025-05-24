package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.GetCheckpointConfigureIn;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointConfigureInHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointConfigureModel, GetCheckpointConfigureIn>
{
    private final ICheckpointConfigurePort checkpointConfigurePort;

    @Override
    public void init() {
        register(GetCheckpointConfigureIn.class,this);
    }

    @Override
    public Collection<CheckpointConfigureModel> handle(GetCheckpointConfigureIn useCase) {
        return checkpointConfigurePort.findByCodeIn(useCase.getConfigureCodes());
    }
}
