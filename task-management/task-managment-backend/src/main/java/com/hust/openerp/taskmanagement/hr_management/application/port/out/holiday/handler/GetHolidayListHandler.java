package com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IHolidayPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.GetHolidayList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayListModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetHolidayListHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<HolidayListModel, GetHolidayList>
{
    private final IHolidayPort holidayPort;

    @Override
    public void init() {
        register(GetHolidayList.class,this);
    }

    @Override
    public HolidayListModel handle(GetHolidayList useCase) {
        return HolidayListModel.populate(
            holidayPort.getHolidays(useCase.getStartDate(), useCase.getEndDate()),
            useCase.getStartDate(),
            useCase.getEndDate()
        );
    }
}
