package openerp.openerpresourceserver.application.port.out.department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IDepartmentPort;
import openerp.openerpresourceserver.application.port.out.department.usecase_data.GetDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

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
