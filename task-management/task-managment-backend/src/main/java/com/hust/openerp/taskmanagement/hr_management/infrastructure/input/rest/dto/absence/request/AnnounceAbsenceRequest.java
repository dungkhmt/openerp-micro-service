package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.AnnounceAbsence;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AnnounceAbsenceRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private String reason;
    @NotNull
    private AbsenceType type;

    public AnnounceAbsence toUseCase(String userId){
        return AnnounceAbsence.builder()
            .date(date)
            .userId(userId)
            .startTime(startTime)
            .endTime(endTime)
            .reason(reason)
            .type(type)
            .build();
    }
}
