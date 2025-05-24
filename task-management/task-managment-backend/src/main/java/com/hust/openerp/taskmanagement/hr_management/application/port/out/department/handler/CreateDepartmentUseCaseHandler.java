package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator.ICodeGeneratorService;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.service.DepartmentValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.CreateDepartment;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@Slf4j
public class CreateDepartmentUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<CreateDepartment> {
    private final IDepartmentPort departmentPort;
    private final ICodeGeneratorService codeGeneratorService;
    private final DepartmentValidator validator;

    public CreateDepartmentUseCaseHandler(
            IDepartmentPort departmentPort,
            @Qualifier("departmentCodeGenerator") ICodeGeneratorService codeGeneratorService,
            DepartmentValidator validator) {
        this.departmentPort = departmentPort;
        this.codeGeneratorService = codeGeneratorService;
        this.validator = validator;
    }

    @Override
    public void init() {
        register(CreateDepartment.class,this);
    }

    @Override
    public void handle(CreateDepartment useCase) {
        validator.validateDepartmentName(useCase.getDepartmentName());
        var model = useCase.toModel();
        var code = codeGeneratorService.generateCode(departmentPort);
        model.setDepartmentCode(code);
        departmentPort.createDepartment(model);
    }
}
