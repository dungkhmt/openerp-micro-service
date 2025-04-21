package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.UpdateAbsence;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetAbsenceListRequest {
    private UUID id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;

    public UpdateAbsence toUseCase(String userId){
        return UpdateAbsence.builder()
            .date(date)
            .startTime(startTime)
            .endTime(endTime)
            .reason(reason)
            .userId(userId)
            .build();
    }
}
