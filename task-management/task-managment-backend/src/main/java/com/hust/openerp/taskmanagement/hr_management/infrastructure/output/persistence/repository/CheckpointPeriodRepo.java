package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointPeriodEntity;

import java.util.List;
import java.util.UUID;

public interface CheckpointPeriodRepo extends IBaseRepository<CheckpointPeriodEntity, UUID> {
    List<CheckpointPeriodEntity> findByIdIn(List<UUID> ids);
}
