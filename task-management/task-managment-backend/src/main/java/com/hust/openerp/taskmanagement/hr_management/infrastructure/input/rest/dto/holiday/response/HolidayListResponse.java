package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayListModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HolidayListResponse {
    private Map<LocalDate, HolidayResponse> holidays;

    public static HolidayListResponse fromModel(HolidayListModel model) {
        Map<LocalDate, HolidayResponse> holidayResponseMap = new HashMap<>();

        for (Map.Entry<LocalDate, HolidayModel> entry : model.getHolidays().entrySet()) {
            LocalDate date = entry.getKey();
            HolidayModel holidayModel = entry.getValue();
            HolidayResponse holidayResponse = HolidayResponse.fromModel(holidayModel);
            holidayResponseMap.put(date, holidayResponse);
        }

        return new HolidayListResponse(holidayResponseMap);
    }
}
