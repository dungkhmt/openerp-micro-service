package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IHolidayPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.GetHoliday;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetHolidayHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<HolidayModel, GetHoliday>
{
    private final IHolidayPort holidayPort;

    @Override
    public void init() {
        register(GetHoliday.class,this);
    }

    @Override
    public HolidayModel handle(GetHoliday useCase) {
        return holidayPort.getHoliday(useCase.getId());
    }
}
