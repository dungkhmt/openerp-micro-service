package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.TimePerformance;

import java.util.List;

public interface TimePerformanceService {
    List<TimePerformance> getTimePerformance(FilterScheduleDto requestDto);

    List<TimePerformance> getAll();
}
