package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class AssignJobPositionHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AssignJobPosition> {
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(AssignJobPosition.class,this);
    }

    @Override
    public void handle(AssignJobPosition useCase) {
        staffJobPositionPort.assignJobPosition(useCase.getUserLoginId(), useCase.getJobPositionCode());
    }
}
