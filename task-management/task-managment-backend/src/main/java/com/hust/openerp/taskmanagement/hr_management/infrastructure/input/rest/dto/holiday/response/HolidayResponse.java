package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HolidayResponse {
    private UUID id;
    private String name;
    private HolidayType type;
    private List<LocalDate> dates;

    public static HolidayResponse fromModel(HolidayModel model) {
        return HolidayResponse.builder()
            .id(model.getId())
            .name(model.getName())
            .type(model.getType())
            .dates(model.getDates())
            .build();
    }
}
