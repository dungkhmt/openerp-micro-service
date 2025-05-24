package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
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
public class AbsenceResponse {
    private UUID id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private AbsenceStatus status;
    private AbsenceType type;
    private String userId;

    public static AbsenceResponse fromModel(AbsenceModel model) {
        return AbsenceResponse.builder()
            .id(model.getId())
            .date(model.getDate())
            .startTime(model.getStartTime())
            .endTime(model.getEndTime())
            .reason(model.getReason())
            .status(model.getStatus())
            .userId(model.getUserId())
            .type(model.getType())
            .build();
    }

    public static List<AbsenceResponse> fromModels(Collection<AbsenceModel> models) {
        return models.stream().map(AbsenceResponse::fromModel).toList();
    }
}
