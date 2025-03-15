package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckinoutPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.MonthAttendance;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AttendanceModel;

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
