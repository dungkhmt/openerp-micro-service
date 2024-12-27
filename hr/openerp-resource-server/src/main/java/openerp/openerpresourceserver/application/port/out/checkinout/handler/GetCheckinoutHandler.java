package openerp.openerpresourceserver.application.port.out.checkinout.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckinoutPort;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.GetCheckinout;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckinoutHandler extends ObservableUseCasePublisher implements CollectionUseCaseHandler<CheckinoutModel, GetCheckinout> {
    private final ICheckinoutPort checkinoutPort;

    @Override
    public void init() {
        register(GetCheckinout.class, this);
    }

    @Override
    public Collection<CheckinoutModel> handle(GetCheckinout useCase) {
        return checkinoutPort.getCheckinout(useCase);
    }
}
