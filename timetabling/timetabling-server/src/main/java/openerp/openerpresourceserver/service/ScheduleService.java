package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.*;

import java.util.List;

public interface ScheduleService {

    List<Schedule> searchSchedule(FilterScheduleDto requestDto);

    String calculateTimePerformancePerDay(List<Schedule> scheduleList);

    void calculateTimePerformance(FilterScheduleDto requestDto);

    void calculateAllTimePerformance();
}
