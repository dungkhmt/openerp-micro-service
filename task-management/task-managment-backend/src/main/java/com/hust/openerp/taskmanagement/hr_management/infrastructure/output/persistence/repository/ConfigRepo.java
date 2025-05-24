package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ConfigEntity;

import java.util.List;

public interface ConfigRepo extends IBaseRepository<ConfigEntity, String>  {
    List<ConfigEntity> findByConfigGroup(ConfigGroup configGroup);
}

