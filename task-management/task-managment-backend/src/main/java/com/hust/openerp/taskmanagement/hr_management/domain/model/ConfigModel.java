package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfigModel {
    private ConfigKey configKey;
    private String configValue;
    private ConfigType configType;
    private String configName;
    private ConfigGroup configGroup;
    private String description;
}
