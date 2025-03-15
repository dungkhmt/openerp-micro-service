package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.service.CheckpointCalculator;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data.GetCheckpoint;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointModel;

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
