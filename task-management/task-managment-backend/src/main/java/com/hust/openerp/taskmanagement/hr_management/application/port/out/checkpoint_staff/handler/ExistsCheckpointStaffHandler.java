package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.ExistsCheckpointStaff;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class ExistsCheckpointStaffHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<Boolean, ExistsCheckpointStaff>
{
    private final ICheckpointStaffPort checkpointStaffPort;

    @Override
    public void init() {
        register(ExistsCheckpointStaff.class,this);
    }

    @Override
    public Boolean handle(ExistsCheckpointStaff useCase) {
        return checkpointStaffPort.existCheckpointStaff(useCase.getPeriodId());
    }
}
