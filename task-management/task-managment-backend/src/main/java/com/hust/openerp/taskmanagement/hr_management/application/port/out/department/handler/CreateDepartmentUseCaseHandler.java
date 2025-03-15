package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IDepartmentPort;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.application.port.out.department.service.DepartmentValidator;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.CreateDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
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
