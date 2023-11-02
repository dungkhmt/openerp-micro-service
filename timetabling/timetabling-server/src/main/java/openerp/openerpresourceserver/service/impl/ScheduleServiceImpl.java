package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.*;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    public static final Integer MAX_CREW_PER_DAY = 12;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Override
    public List<Schedule> searchSchedule(FilterScheduleDto requestDto) {
        return scheduleRepo.getSchedulesByClassRoomAndStudyWeekAndWeekDay(requestDto.getClassRoom(),
                requestDto.getStudyWeek(), requestDto.getWeekDay());
    }

    @Override
    public String calculateTimePerformance(FilterScheduleDto requestDto) {
        List<Schedule> scheduleList = this.searchSchedule(requestDto);
        int totalTimeUsing = 0;
        for (Schedule schedule : scheduleList) {
            String start = schedule.getStart();
            String finish = schedule.getFinish();
            int countCrew = CommonUtil.calculateTimeCrew(start, finish);
            totalTimeUsing += countCrew;
        }
        double result = (double) totalTimeUsing * 100 / MAX_CREW_PER_DAY;
        double roundedResult = Math.round(result * 100.0) / 100.0;

        return roundedResult + " %";
    }

//    public String calculateSeatPerformance(List<Schedule> scheduleList) {
//
//    }
}
