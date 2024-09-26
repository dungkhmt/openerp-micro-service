package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.entity.WeekDay;
import openerp.openerpresourceserver.generaltimetabling.repo.WeekDayRepo;
import openerp.openerpresourceserver.generaltimetabling.service.WeekDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekDayServiceImpl implements WeekDayService {

    @Autowired
    private WeekDayRepo weekDayRepo;

    @Override
    public List<WeekDay> getWeekDay() {
        return weekDayRepo.findAll();
    }

    @Override
    public void updateWeekDay() {
        List<String> weekDayDataList = weekDayRepo.getWeekDay();
        if (!weekDayDataList.isEmpty()) {
            weekDayRepo.deleteAll();
        }
        List<WeekDay> weekDayList = new ArrayList<>();
        weekDayDataList.forEach(el -> {
            WeekDay weekDay = WeekDay.builder()
                    .weekDay(el)
                    .build();
            weekDayList.add(weekDay);
        });
        weekDayRepo.saveAll(weekDayList);
    }
}
