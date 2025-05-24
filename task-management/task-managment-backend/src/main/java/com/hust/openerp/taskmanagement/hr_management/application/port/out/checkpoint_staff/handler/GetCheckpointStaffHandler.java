package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaff;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
