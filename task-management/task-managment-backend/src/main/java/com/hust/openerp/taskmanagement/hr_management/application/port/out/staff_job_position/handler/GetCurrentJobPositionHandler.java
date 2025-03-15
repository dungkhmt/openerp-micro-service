package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.GetCurrentJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentJobPositionHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffJobPositionModel, GetCurrentJobPosition> {
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(GetCurrentJobPosition.class,this);
    }

    @Override
    public StaffJobPositionModel handle(GetCurrentJobPosition useCase) {
        return staffJobPositionPort.findCurrentJobPosition(useCase.getUserLoginId());
    }
}
