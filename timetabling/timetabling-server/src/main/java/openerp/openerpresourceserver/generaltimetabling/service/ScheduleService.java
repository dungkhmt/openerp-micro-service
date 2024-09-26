package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.RequestPerformanceDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Schedule;

import java.util.List;

public interface ScheduleService {

    List<Schedule> searchSchedule(FilterScheduleDto requestDto);

    String calculateTimePerformancePerDay(List<Schedule> scheduleList);

    void calculateTimePerformance(RequestPerformanceDto requestDto);

    void calculateAllTimePerformance();
}
