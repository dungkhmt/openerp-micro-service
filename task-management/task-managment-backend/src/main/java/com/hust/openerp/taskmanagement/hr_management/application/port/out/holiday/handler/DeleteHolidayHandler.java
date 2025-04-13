package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IHolidayPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.DeleteHoliday;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class DeleteHolidayHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<DeleteHoliday>
{
    private final IHolidayPort holidayPort;

    @Override
    public void init() {
        register(DeleteHoliday.class,this);
    }

    @Override
    public void handle(DeleteHoliday useCase) {
        holidayPort.deleteHoliday(useCase.getId());
    }
}
