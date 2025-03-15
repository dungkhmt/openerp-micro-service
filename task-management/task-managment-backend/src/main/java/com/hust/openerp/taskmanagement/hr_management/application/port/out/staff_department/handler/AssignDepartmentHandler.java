package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffDepartmentPort;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.AssignDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class AssignDepartmentHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AssignDepartment> {
    private final IStaffDepartmentPort staffDepartmentPort;

    @Override
    public void init() {
        register(AssignDepartment.class,this);
    }

    @Override
    public void handle(AssignDepartment useCase) {
        staffDepartmentPort.assignDepartment(useCase.getUserLoginId(), useCase.getDepartmentCode());
    }
}
