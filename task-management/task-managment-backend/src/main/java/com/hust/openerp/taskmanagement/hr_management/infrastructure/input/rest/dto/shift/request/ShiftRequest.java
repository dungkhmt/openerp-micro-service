package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShiftRequest {
    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private LocalDate date;
    private Integer slots;

    public ShiftModel toModel(){
        return ShiftModel.builder()
            .date(date)
            .userId(userId)
            .startTime(startTime)
            .endTime(endTime)
            .note(note)
            .slots(slots)
            .build();
    }
}

