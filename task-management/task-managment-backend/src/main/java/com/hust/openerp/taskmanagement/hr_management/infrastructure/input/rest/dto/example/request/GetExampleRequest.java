package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.example.request;

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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetExampleRequest {

    public GetCheckinout toUseCase(){
        return GetCheckinout.builder()
                .build();
    }
}
