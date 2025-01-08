package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.WeekDay;

import java.util.List;

public interface WeekDayService {

    List<WeekDay> getWeekDay();

    void updateWeekDay();
}
