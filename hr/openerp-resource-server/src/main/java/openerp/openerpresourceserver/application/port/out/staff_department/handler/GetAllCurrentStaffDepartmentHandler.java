package openerp.openerpresourceserver.application.port.out.staff_department.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.GetAllCurrentStaffDepartment;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffDepartmentHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffJobPositionModel, GetAllCurrentStaffDepartment>
{
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(GetAllCurrentStaffDepartment.class, this);
    }

    @Override
    public Collection<StaffJobPositionModel> handle(GetAllCurrentStaffDepartment useCase) {
        return staffJobPositionPort.findCurrentJobPositionIn(useCase.getUserLoginIds());
    }
}
