package openerp.openerpresourceserver.application.port.out.staff_department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffDepartmentPort;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.GetAllCurrentStaffDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffDepartmentHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffDepartmentModel, GetAllCurrentStaffDepartment>
{
    private final IStaffDepartmentPort staffDepartmentPort;

    @Override
    public void init() {
        register(GetAllCurrentStaffDepartment.class, this);
    }

    @Override
    public Collection<StaffDepartmentModel> handle(GetAllCurrentStaffDepartment useCase) {
        return staffDepartmentPort.findCurrentDepartmentIn(useCase.getUserLoginIds());
    }
}
