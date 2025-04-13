package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import java.util.List;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import java.time.LocalDate;
import java.util.UUID;


public interface IHolidayPort {
    HolidayModel createHoliday(HolidayModel holidayModel);
    HolidayModel updateHoliday(HolidayModel holidayModel);
    void deleteHoliday(UUID id);
    HolidayModel getHoliday(UUID id);
    List<HolidayModel> getHolidays(LocalDate startDate, LocalDate endDate);
}