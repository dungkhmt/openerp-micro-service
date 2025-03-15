package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkinout.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.GetCheckinout;
import openerp.openerpresourceserver.constant.CheckinoutType;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCheckinoutRequest {
    private String userId;
    private LocalDate date;
    private CheckinoutType type;

    public GetCheckinout toUseCase(){
        return GetCheckinout.builder()
                .userId(this.getUserId())
                .date(this.getDate())
                .type(this.getType())
                .build();
    }
}
