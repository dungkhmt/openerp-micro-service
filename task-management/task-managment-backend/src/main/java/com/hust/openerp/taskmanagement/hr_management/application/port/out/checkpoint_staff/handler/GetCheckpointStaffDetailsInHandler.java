package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.GetCheckpointStaffDetailsIn;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointStaffDetailsModel;

import java.util.Collection;

@DomainComponent
@RequiredArgsConstructor
@Slf4j
public class GetCheckpointStaffDetailsInHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<CheckpointStaffDetailsModel, GetCheckpointStaffDetailsIn> {
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(GetCheckpointStaffDetailsIn.class,this);
    }

    @Override
    public Collection<CheckpointStaffDetailsModel> handle(GetCheckpointStaffDetailsIn useCase) {
        return checkpointStaffPort.getCheckpointStaffDetailsIn(useCase.getPeriodId(), useCase.getUserIds());
    }
}
