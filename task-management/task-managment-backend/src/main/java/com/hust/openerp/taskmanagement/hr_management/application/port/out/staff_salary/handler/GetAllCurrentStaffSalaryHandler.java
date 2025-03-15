package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffSalaryPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetAllCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;

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
