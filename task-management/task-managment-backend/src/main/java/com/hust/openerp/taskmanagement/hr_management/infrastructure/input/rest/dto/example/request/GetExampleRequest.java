package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.example.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetCheckinout;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetExampleRequest {

    public GetCheckinout toUseCase(){
        return GetCheckinout.builder()
                .build();
    }
}
