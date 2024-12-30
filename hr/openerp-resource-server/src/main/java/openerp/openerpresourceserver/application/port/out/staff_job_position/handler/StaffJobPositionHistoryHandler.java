package openerp.openerpresourceserver.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.StaffJobPositionHistory;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class StaffJobPositionHistoryHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffJobPositionModel, StaffJobPositionHistory>
{
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(StaffJobPositionHistory.class, this);
    }

    @Override
    public Collection<StaffJobPositionModel> handle(StaffJobPositionHistory useCase) {
        return staffJobPositionPort.findJobPositionHistory(useCase.getUserLoginId());
    }
}
