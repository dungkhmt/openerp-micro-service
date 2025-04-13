package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.holiday.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.CreateDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.CreateHoliday;
import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateHolidayRequest {
    @NotNull
    private String name;
    @NotNull
    private HolidayType type;
    private List<LocalDate> dates;

    public CreateHoliday toUseCase(){
        return CreateHoliday.builder()
            .name(name)
            .type(type)
            .dates(dates)
            .build();
    }
}
