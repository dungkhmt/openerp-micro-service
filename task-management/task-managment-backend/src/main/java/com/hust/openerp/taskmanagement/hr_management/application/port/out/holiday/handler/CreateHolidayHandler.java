package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IHolidayPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.CreateHoliday;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CreateHolidayHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<CreateHoliday> {
    private final IHolidayPort holidayPort;

    @Override
    public void init() {
        register(CreateHoliday.class,this);
    }

    @Override
    public void handle(CreateHoliday useCase) {
        holidayPort.createHoliday(useCase.toModel());
    }
}
