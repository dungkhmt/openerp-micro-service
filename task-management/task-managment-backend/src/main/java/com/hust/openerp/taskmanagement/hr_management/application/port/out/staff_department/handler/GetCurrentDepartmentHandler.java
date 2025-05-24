package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.GetCurrentDepartment;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffDepartmentModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentDepartmentHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffDepartmentModel, GetCurrentDepartment> {
    private final IStaffDepartmentPort staffDepartmentPort;

    @Override
    public void init() {
        register(GetCurrentDepartment.class,this);
    }

    @Override
    public StaffDepartmentModel handle(GetCurrentDepartment useCase) {
        return staffDepartmentPort.findCurrentDepartment(useCase.getUserLoginId());
    }
}
