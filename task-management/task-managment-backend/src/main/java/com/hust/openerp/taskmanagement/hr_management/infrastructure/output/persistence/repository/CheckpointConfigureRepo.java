package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointConfigureEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CheckpointConfigureRepo extends IBaseRepository<CheckpointConfigureEntity, String> {
    @Query("SELECT MAX(c.checkpointCode) FROM CheckpointConfigureEntity c " +
            "WHERE c.checkpointCode LIKE CONCAT(:prefix, '%')")
    String findMaxCode(@Param("prefix") String prefix);

    List<CheckpointConfigureEntity> findByCheckpointCodeIn(List<String> checkpointCodes);
}
