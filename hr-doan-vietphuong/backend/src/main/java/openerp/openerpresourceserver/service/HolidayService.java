package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayQueryRequest;
import openerp.openerpresourceserver.dto.request.holiday.HolidayRequest;
import openerp.openerpresourceserver.entity.Holiday;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    Page<Holiday> getHolidaysByProperties(HolidayQueryRequest dto, PagingRequest pagingRequest);

    Holiday getHolidayById(Long id);

    Holiday createHoliday(HolidayRequest dto) throws BadRequestException;

    Holiday editHoliday(HolidayRequest dto) throws BadRequestException;

    List<Holiday> deleteHolidays(List<Long> idList);

    List<Integer> getAttendanceHoliday(final LocalDate startDate, final LocalDate endDate);

    double getBonusTime(int date);

    Holiday getHolidayByDate(int type, int date);
}
