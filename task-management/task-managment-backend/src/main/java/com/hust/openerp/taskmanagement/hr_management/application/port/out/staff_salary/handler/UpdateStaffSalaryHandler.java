package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffSalaryPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.UpdateStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;

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
