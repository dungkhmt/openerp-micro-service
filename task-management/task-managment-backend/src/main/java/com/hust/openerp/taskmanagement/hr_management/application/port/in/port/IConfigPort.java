package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CompanyConfigModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ConfigModel;

import java.util.Map;

public interface IConfigPort {
    Map<ConfigKey, ConfigModel> getConfigsInGroup(ConfigGroup configGroup);
    void updateConfigs(Map<ConfigKey, String> updates);
    CompanyConfigModel getCompanyConfig();
}
