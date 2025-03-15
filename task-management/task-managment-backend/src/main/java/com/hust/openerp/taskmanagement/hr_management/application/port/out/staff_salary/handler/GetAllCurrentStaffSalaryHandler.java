package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffSalaryPort;
import openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data.GetAllCurrentStaffSalary;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffSalaryModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffSalaryHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffSalaryModel, GetAllCurrentStaffSalary>
{
    private final IStaffSalaryPort staffSalaryPort;

    @Override
    public void init() {
        register(GetAllCurrentStaffSalary.class, this);
    }

    @Override
    public Collection<StaffSalaryModel> handle(GetAllCurrentStaffSalary useCase) {
        return staffSalaryPort.findCurrentSalaryIn(useCase.getUserLoginIds());
    }
}
