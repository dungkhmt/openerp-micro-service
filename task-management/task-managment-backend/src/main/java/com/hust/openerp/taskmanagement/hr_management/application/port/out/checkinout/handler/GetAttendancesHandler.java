package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckinoutPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetAttendances;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AttendanceModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAttendancesHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<AttendanceModel, GetAttendances>
{
    private final ICheckinoutPort checkinoutPort;
    private final IConfigPort configPort;

    @Override
    public void init() {
        register(GetAttendances.class, this);
    }

    @Override
    public Collection<AttendanceModel> handle(GetAttendances useCase) {
        var companyConfig = configPort.getCompanyConfig();
        return AttendanceModel.populateFrom(checkinoutPort.getCheckinout(useCase), companyConfig);
    }
}
