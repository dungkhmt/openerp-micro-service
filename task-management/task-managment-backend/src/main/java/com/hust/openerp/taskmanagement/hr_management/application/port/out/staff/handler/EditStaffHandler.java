package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.staff.service.StaffValidator;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.EditStaff;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.AssignDepartment;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class EditStaffHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<EditStaff> {
    private final IStaffPort staffPort;
    private final StaffValidator staffValidator;

    @Override
    public void init() {
        register(EditStaff.class,this);
    }

    @Override
    public void handle(EditStaff useCase) {
        if(useCase.getFullName() != null){
            staffValidator.validateStaffName(useCase.getFullName());
        }
        var editedModel = staffPort.editStaff(useCase.toModel());
        if(useCase.getDepartmentCode() != null){
            assignStaffDepartment(useCase.getDepartmentCode(), editedModel.getUserLoginId());
        }
        if(useCase.getJobPositionCode() != null){
            assignStaffJobPosition(useCase.getJobPositionCode(), editedModel.getUserLoginId());
        }
    }

    private void assignStaffDepartment(String departmentCode, String userLoginId){
        var assignDepartmentUseCase = AssignDepartment.builder()
                .departmentCode(departmentCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignDepartmentUseCase);
    }

    private void assignStaffJobPosition(String jobPositionCode, String userLoginId){
        var assignJobPosition = AssignJobPosition.builder()
                .jobPositionCode(jobPositionCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignJobPosition);
    }


}


