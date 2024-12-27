package openerp.openerpresourceserver.application.port.out.department_detail.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.department_detail.usecase_data.AssignJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class AssignJobPositionHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AssignJobPosition> {

    @Override
    public void init() {
        register(AssignJobPosition.class,this);
    }

    @Override
    public void handle(AssignJobPosition useCase) {

    }
}
