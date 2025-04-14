package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.UpdateHoliday;
import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateHolidayRequest {
    @NotNull
    private String name;
    @NotNull
    private HolidayType type;
    private List<LocalDate> dates;

    public UpdateHoliday toUseCase(UUID id){
        return UpdateHoliday.builder()
            .id(id)
            .name(name)
            .type(type)
            .dates(dates)
            .build();
    }
}
