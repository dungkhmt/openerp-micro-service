package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointStaffHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffModel, GetCheckpointStaff>
{
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetCheckpointStaff.class,this);
    }

    @Override
    public Collection<CheckpointStaffModel> handle(GetCheckpointStaff useCase) {
        return checkpointStaffPort.getAllCheckpointStaff(useCase);
    }
}
