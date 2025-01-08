package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.TimePerformance;

import java.util.List;

public interface TimePerformanceService {
    List<TimePerformance> getTimePerformance(FilterScheduleDto requestDto);

    List<TimePerformance> getAll();

    void deleteByIdList(List<Long> idList);
}
