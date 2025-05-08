package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.UpdateAbsence;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateAbsenceRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AbsenceType type;
    private String reason;

    public UpdateAbsence toUseCase(String userId, UUID id){
        return UpdateAbsence.builder()
            .id(id)
            .date(date)
            .startTime(startTime)
            .endTime(endTime)
            .reason(reason)
            .type(type)
            .userId(userId)
            .build();
    }
}
