package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IDepartmentPort;
import openerp.openerpresourceserver.application.port.out.department.service.DepartmentValidator;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.UpdateDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateDepartmentUseCaseHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateDepartment>
{
    private final IDepartmentPort departmentPort;
    private final DepartmentValidator validator;

    @Override
    public void init() {
        register(UpdateDepartment.class,this);
    }

    @Override
    public void handle(UpdateDepartment useCase) {
        var model = useCase.toModel();
        if(useCase.getDepartmentName() != null){
            validator.validateDepartmentName(useCase.getDepartmentName());
        }
        departmentPort.updateDepartment(model);
    }
}
