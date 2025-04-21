package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CompanyConfigModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ConfigModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ConfigEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.ConfigRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConfigAdapter implements IConfigPort {
    private final ConfigRepo configRepo;

    @Override
    public Map<ConfigKey, ConfigModel> getConfigsInGroup(ConfigGroup configGroup){
        var entities = configRepo.findByConfigGroup(configGroup);
        return toMapModel(entities);
    }

    @Override
    public void updateConfigs(Map<ConfigKey, String> updates) {
        List<ConfigEntity> updatedEntities = new ArrayList<>();

        for (Map.Entry<ConfigKey, String> entry : updates.entrySet()) {
            ConfigKey key = entry.getKey();
            String newValue = entry.getValue();

            ConfigEntity entity = configRepo.findById(key.name())
                .orElseThrow(() -> new ApplicationException(ResponseCode.CONFIG_NOT_EXISTED ,"Config key not found: " + key));

            if (entity.getConfigType().isValidFormat(newValue)) {
                entity.setConfigValue(newValue);
                updatedEntities.add(entity);
            } else {
                throw new ApplicationException(ResponseCode.FORMAT_CONFIG_ERROR ,"Invalid format for key: " + key);
            }
        }

        configRepo.saveAll(updatedEntities);
    }

    @Override
    public CompanyConfigModel getCompanyConfig() {
        var entities = configRepo.findByConfigGroup(ConfigGroup.COMPANY_CONFIGS);
        var configMap = toMapModel(entities);
        try {
            return CompanyConfigModel.builder()
                .startWorkTime(LocalTime.parse(configMap.get(ConfigKey.START_WORK_TIME).getConfigValue()))
                .endWorkTime(LocalTime.parse(configMap.get(ConfigKey.END_WORK_TIME).getConfigValue()))
                .startLunchTime(LocalTime.parse(configMap.get(ConfigKey.START_LUNCH_TIME).getConfigValue()))
                .endLunchTime(LocalTime.parse(configMap.get(ConfigKey.END_LUNCH_TIME).getConfigValue()))
                .hourBeforeAnnounceAbsence(Float.parseFloat(configMap.get(ConfigKey.HOUR_BEFORE_ANNOUNCE_ABSENCE).getConfigValue()))
                .build();
        }
        catch (Exception e) {
            throw new ApplicationException(ResponseCode.GET_COMPANY_CONFIG_ERROR, "Get Company Config ERROR: " + e.getMessage());
        }
    }

    private Map<ConfigKey, ConfigModel> toMapModel(List<ConfigEntity> entities){
        return entities.stream()
            .collect(Collectors.toMap(
                entity -> ConfigKey.valueOf(entity.getConfigKey()),
                this::toModel
            ));
    }

    private ConfigModel toModel(ConfigEntity entity){
        return ConfigModel.builder()
            .configKey(ConfigKey.valueOf(entity.getConfigKey()))
            .configValue(entity.getConfigValue())
            .configType(entity.getConfigType())
            .configName(entity.getConfigName())
            .configGroup(entity.getConfigGroup())
            .description(entity.getDescription())
            .build();
    }

}
