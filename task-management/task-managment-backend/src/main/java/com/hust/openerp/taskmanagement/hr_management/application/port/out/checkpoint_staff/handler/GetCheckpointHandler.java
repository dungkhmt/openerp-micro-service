package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.service.CheckpointCalculator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.GetCheckpoint;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckpointHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<CheckpointModel, GetCheckpoint>
{
    private final ICheckpointStaffPort checkpointStaffPort;
    private final CheckpointCalculator checkpointCalculator;

    @Override
    public void init() {
        register(GetCheckpoint.class,this);
    }

    @Override
    public CheckpointModel handle(GetCheckpoint useCase) {
        var checkpointStaffList = checkpointStaffPort.getAllCheckpointStaff(useCase);
        BigDecimal totalPoint = null;

        if(!checkpointStaffList.isEmpty()) {
            totalPoint = checkpointCalculator.calculatePoint(this, useCase.getPeriodId(), checkpointStaffList);
        }

        return CheckpointModel.builder()
                .userId(useCase.getUserId())
                .totalPoint(totalPoint)
                .checkpointStaffs(checkpointStaffList)
                .periodId(useCase.getPeriodId())
                .build();
    }
}
