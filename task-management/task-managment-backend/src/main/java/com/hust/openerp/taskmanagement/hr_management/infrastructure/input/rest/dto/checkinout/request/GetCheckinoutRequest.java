package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetCheckinout;
import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCheckinoutRequest {
    @NotNull
    private LocalDate date;
    private CheckinoutType type;

    public GetCheckinout toUseCase(String userId){
        return GetCheckinout.builder()
                .userId(userId)
                .date(this.getDate())
                .type(this.getType())
                .build();
    }
}
