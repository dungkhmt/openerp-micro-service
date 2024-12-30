package openerp.openerpresourceserver.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class AssignJobPositionHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AssignJobPosition> {
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(AssignJobPosition.class,this);
    }

    @Override
    public void handle(AssignJobPosition useCase) {
        staffJobPositionPort.assignJobPosition(useCase.getUserLoginId(), useCase.getJobPositionCode());
    }
}
