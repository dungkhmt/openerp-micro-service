package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.GetAllCurrentStaffDepartment;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffDepartmentModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffDepartmentHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffDepartmentModel, GetAllCurrentStaffDepartment>
{
    private final IStaffDepartmentPort staffDepartmentPort;

    @Override
    public void init() {
        register(GetAllCurrentStaffDepartment.class, this);
    }

    @Override
    public Collection<StaffDepartmentModel> handle(GetAllCurrentStaffDepartment useCase) {
        return staffDepartmentPort.findCurrentDepartmentIn(useCase.getUserLoginIds());
    }
}
