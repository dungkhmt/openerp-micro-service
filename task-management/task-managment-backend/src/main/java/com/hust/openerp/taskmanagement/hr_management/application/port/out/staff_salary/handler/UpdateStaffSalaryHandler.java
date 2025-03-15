package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffSalaryPort;
import openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data.UpdateStaffSalary;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateStaffSalaryHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<UpdateStaffSalary> {
    private final IStaffSalaryPort staffSalaryPort;

    @Override
    public void init() {
        register(UpdateStaffSalary.class,this);
    }

    @Override
    public void handle(UpdateStaffSalary useCase) {
        staffSalaryPort.updateSalary(useCase.toModel());
    }
}
