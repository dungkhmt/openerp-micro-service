package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShiftResponse {
    private UUID id;
    private String userId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private LocalDate date;
    private Integer slots;

    public static ShiftResponse fromModel(ShiftModel model) {
        return ShiftResponse.builder()
            .id(model.getId())
            .userId(model.getUserId())
            .startTime(model.getStartTime())
            .endTime(model.getEndTime())
            .note(model.getNote())
            .date(model.getDate())
            .slots(model.getSlots())
            .build();
    }

    public static List<ShiftResponse> fromModels(Collection<ShiftModel> models) {
        return models.stream().map(ShiftResponse::fromModel).toList();
    }
}
