package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.WeekDay;

import java.util.List;

public interface WeekDayService {

    List<WeekDay> getWeekDay();

    void updateWeekDay();
}
