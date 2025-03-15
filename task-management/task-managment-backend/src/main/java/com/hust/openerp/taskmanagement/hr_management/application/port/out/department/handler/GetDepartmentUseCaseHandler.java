package com.hust.openerp.taskmanagement.hr_management.application.port.out.department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.GetDepartment;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetDepartmentUseCaseHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<DepartmentModel, GetDepartment>
{
    private final IDepartmentPort departmentPort;

    @Override
    public void init() {
        register(GetDepartment.class,this);
    }

    @Override
    public PageWrapper<DepartmentModel> handle(GetDepartment useCase) {
        return departmentPort.getDepartment(useCase, useCase.getPageableRequest());
    }
}
