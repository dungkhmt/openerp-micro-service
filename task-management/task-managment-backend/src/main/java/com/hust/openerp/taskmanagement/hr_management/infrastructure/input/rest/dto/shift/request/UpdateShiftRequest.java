package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.UpdateShift;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateShiftRequest {
    @NotNull
    private UUID id;
    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private LocalDate date;
    private Integer slots;

    public UpdateShift toUseCase(){
        return new UpdateShift(
            ShiftModel.builder()
                .id(id)
                .date(date)
                .note(note)
                .slots(slots)
                .userId(userId)
                .startTime(startTime)
                .endTime(endTime)
                .build()
        );
    }
}
