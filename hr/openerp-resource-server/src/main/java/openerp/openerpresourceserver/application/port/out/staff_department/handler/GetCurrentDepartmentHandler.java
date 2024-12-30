package openerp.openerpresourceserver.application.port.out.staff_department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffDepartmentPort;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.GetCurrentDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;

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
