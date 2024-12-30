package openerp.openerpresourceserver.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.GetCurrentJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentJobPositionHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffJobPositionModel, GetCurrentJobPosition> {
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(GetCurrentJobPosition.class,this);
    }

    @Override
    public StaffJobPositionModel handle(GetCurrentJobPosition useCase) {
        return staffJobPositionPort.findCurrentJobPosition(useCase.getUserLoginId());
    }
}
