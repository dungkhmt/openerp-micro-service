package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffSalaryPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentStaffSalaryHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffSalaryModel, GetCurrentStaffSalary> {
    private final IStaffSalaryPort staffSalaryPort;

    @Override
    public void init() {
        register(GetCurrentStaffSalary.class,this);
    }

    @Override
    public StaffSalaryModel handle(GetCurrentStaffSalary useCase) {
        return staffSalaryPort.findCurrentSalary(useCase.getUserLoginId());
    }
}
