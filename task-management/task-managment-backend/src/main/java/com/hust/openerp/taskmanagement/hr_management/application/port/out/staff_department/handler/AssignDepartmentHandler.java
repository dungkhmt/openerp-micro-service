package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.AssignDepartment;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
