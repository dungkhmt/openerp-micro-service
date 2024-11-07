package openerp.openerpresourceserver.application.port.out.checkinout.handler;

import openerp.openerpresourceserver.application.port.out.checkinout.data.Checkinout;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
public class CheckinoutUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<Checkinout>{

    @Override
    public void init() {
        register(Checkinout.class, this);
    }

    @Override
    public void handle(Checkinout useCase) {

    }
}
