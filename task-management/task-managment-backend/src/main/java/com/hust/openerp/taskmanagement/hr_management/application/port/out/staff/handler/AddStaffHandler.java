package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator.ICodeGeneratorService;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.service.StaffValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.AddStaff;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.AssignDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.UpdateStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@Slf4j
public class AddStaffHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AddStaff> {
    private final IStaffPort staffPort;
    private final StaffValidator staffValidator;
    private final ICodeGeneratorService codeGeneratorService;

    public AddStaffHandler(
            IStaffPort staffPort,
            StaffValidator staffValidator,
            @Qualifier("staffCodeGenerator") ICodeGeneratorService codeGeneratorService) {
        this.staffPort = staffPort;
        this.staffValidator = staffValidator;
        this.codeGeneratorService = codeGeneratorService;
    }

    @Override
    public void init() {
        register(AddStaff.class,this);
    }

    @Override
    @Transactional
    public void handle(AddStaff useCase) {
        var model = useCase.toModel();
        staffValidator.validateStaffName(model.getFullname());
        var code = codeGeneratorService.generateCode(staffPort);
        model.setStaffCode(code);
        var savedModel = staffPort.addStaff(model);
        addStaffDepartment(useCase.getDepartmentCode(), savedModel.getUserLoginId());
        addStaffJobPosition(useCase.getJobPositionCode(), savedModel.getUserLoginId());
        if(useCase.getSalaryType() != null && useCase.getSalary() != null){
            addStaffSalary(useCase.getSalaryType(), useCase.getSalary(), savedModel.getUserLoginId());

        }
    }

    private void addStaffDepartment(String departmentCode, String userLoginId){
        var assignDepartmentUseCase = AssignDepartment.builder()
                .departmentCode(departmentCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignDepartmentUseCase);
    }

    private void addStaffJobPosition(String jobPositionCode, String userLoginId){
        var assignJobPosition = AssignJobPosition.builder()
                .jobPositionCode(jobPositionCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignJobPosition);
    }

    private void addStaffSalary(SalaryType salaryType, Integer salary, String userLoginId){
        var assignJobPosition = UpdateStaffSalary.builder()
            .salary(salary)
            .salaryType(salaryType)
            .userLoginId(userLoginId)
            .build();
        publish(assignJobPosition);
    }
}
