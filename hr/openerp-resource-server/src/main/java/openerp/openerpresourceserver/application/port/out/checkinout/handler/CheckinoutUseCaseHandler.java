package openerp.openerpresourceserver.application.port.out.checkinout.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckinoutPort;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;

import java.time.LocalDateTime;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CheckinoutUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<Checkinout>{
    private final ICheckinoutPort checkinoutPort;

    @Override
    public void init() {
        register(Checkinout.class, this);
    }

    @Override
    public void handle(Checkinout useCase) {
        checkinoutPort.checkinout(useCase);
    }
}
