package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.AnnounceAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.UpdateConfigs;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AnnounceAbsenceRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;

    public AnnounceAbsence toModel(String userId){
        return AnnounceAbsence.builder()
            .date(date)
            .startTime(startTime)
            .endTime(endTime)
            .reason(reason)
            .build();
    }
}
