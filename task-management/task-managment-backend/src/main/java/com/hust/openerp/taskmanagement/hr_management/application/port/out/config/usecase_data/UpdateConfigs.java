package com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Builder
@Getter
@Setter
public class UpdateConfigs implements UseCase {
    Map<ConfigKey, String> configValueMap;
}
