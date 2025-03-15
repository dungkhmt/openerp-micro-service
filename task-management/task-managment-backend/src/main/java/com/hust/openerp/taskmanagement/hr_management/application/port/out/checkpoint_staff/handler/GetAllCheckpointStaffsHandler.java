package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.GetAllCheckpointStaffs;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffModel;

import java.util.Collection;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetAllCheckpointStaffsHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffModel, GetAllCheckpointStaffs> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetAllCheckpointStaffs.class,this);
    }

    @Override
    public Collection<CheckpointStaffModel> handle(GetAllCheckpointStaffs useCase) {
        return checkpointStaffPort.getAllCheckpointStaff(useCase);
    }
}
