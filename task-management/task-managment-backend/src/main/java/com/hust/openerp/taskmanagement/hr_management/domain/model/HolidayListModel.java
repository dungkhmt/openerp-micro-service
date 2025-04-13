package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HolidayListModel {
    private Map<LocalDate, HolidayModel> holidays;

    public static HolidayListModel populate(Collection<HolidayModel> models, LocalDate startDate, LocalDate endDate) {
        var holidayMap = new HashMap<LocalDate, HolidayModel>();
        List<Integer> years = new ArrayList<>();
        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            years.add(year);
        }
        models.forEach(
            model -> {
                List<LocalDate> dates = new ArrayList<>();
                if(model.getType() == HolidayType.EVERY_YEAR){
                    for (var year : years){
                        for(LocalDate date : model.getDates()){
                            var datePopulated = LocalDate.of(year, date.getMonth(), date.getDayOfMonth());
                            dates.add(datePopulated);
                        }
                    }
                }
                else dates = model.getDates();
                for (var date : dates){
                    holidayMap.put(date, model);
                }
            }
        );
        return new HolidayListModel(holidayMap);
    }
}
