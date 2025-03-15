package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.service.StaffValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.EditStaff;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.AssignDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;

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


