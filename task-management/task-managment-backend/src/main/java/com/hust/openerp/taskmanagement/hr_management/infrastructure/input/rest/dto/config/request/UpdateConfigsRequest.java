package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.config.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.UpdateConfigs;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.UpdateHoliday;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateConfigsRequest {
    private Map<ConfigKey, String> configMap;

    public UpdateConfigs toUseCase(){
        return UpdateConfigs.builder()
            .configValueMap(configMap)
            .build();
    }
}

