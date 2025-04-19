package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.config.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigType;
import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ConfigModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.HolidayModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConfigResponse {
    private ConfigKey configKey;
    private String configValue;
    private ConfigType configType;
    private String configName;
    private ConfigGroup configGroup;
    private String description;

    public static ConfigResponse fromModel(ConfigModel model) {
        return ConfigResponse.builder()
            .configKey(model.getConfigKey())
            .configValue(model.getConfigValue())
            .configType(model.getConfigType())
            .configName(model.getConfigName())
            .configGroup(model.getConfigGroup())
            .description(model.getDescription())
            .build();
    }
}
