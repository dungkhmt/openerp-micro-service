package openerp.openerpresourceserver.application.port.out.checkinout.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckinoutPort;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.MonthAttendance;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.CollectionUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.model.AttendanceModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class MonthAttendanceHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<AttendanceModel, MonthAttendance>
{
    private final ICheckinoutPort checkinoutPort;

    @Override
    public void init() {
        register(MonthAttendance.class, this);
    }

    @Override
    public Collection<AttendanceModel> handle(MonthAttendance useCase) {
        return AttendanceModel.populateFrom(checkinoutPort.getCheckinout(useCase));
    }
}
