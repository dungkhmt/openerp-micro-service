package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.CreateShifts;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateShiftsRequest {
    private List<ShiftRequest> shifts;

    public CreateShifts toUseCase(){
        return new CreateShifts(
            shifts.stream().map(ShiftRequest::toModel).toList()
        );
    }
}
