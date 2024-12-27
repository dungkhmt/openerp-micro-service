package openerp.openerpresourceserver.application.port.out.department_detail.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.out.department_detail.usecase_data.GetAllCurrentStaffJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.ExampleModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffJobPositionHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<ExampleModel, GetAllCurrentStaffJobPosition>
{
    @Override
    public void init() {
        register(GetAllCurrentStaffJobPosition.class, this);
    }

    @Override
    public Collection<ExampleModel> handle(GetAllCurrentStaffJobPosition useCase) {
        return null;
    }
}
