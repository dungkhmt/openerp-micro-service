package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ConfigEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.HolidayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConfigRepo extends IBaseRepository<ConfigEntity, String>  {
    List<ConfigEntity> findByConfigGroup(ConfigGroup configGroup);
}

