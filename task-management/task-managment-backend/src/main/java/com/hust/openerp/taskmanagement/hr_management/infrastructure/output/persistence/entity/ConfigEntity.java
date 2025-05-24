package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hr_config")
public class ConfigEntity {
    @Id
    @Column(name = "config_key", nullable = false, length = 50)
    private String configKey;

    @NotNull
    @Column(name = "config_value", nullable = false, length = Integer.MAX_VALUE)
    private String configValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false, length = 30)
    private ConfigType configType;

    @Size(max = 300)
    @NotNull
    @Column(name = "config_name", nullable = false, length = 300)
    private String configName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "config_group", nullable = false, length = 50)
    private ConfigGroup configGroup;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

}