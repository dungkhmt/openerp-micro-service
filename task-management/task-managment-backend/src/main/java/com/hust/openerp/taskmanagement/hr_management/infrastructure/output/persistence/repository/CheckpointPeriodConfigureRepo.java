package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointPeriodConfigureEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointPeriodConfigureId;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.projection.CheckpointPeriodConfigureProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CheckpointPeriodConfigureRepo
        extends IBaseRepository<CheckpointPeriodConfigureEntity, CheckpointPeriodConfigureId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CheckpointPeriodConfigureEntity c WHERE c.id.checkpointPeriodId = :checkpointPeriodId")
    void deleteAllByCheckpointPeriodId(UUID checkpointPeriodId);

    @Query(value = "SELECT c.checkpoint_code AS checkpointCode, " +
            "cc.name AS name, CAST(cc.description AS TEXT) AS description, " +
            "c.coefficient AS coefficient, c.status AS status," +
            "cc.status AS configureStatus " +
            "FROM hr_checkpoint_period_configure c " +
            "JOIN hr_checkpoint_configure cc ON c.checkpoint_code = cc.checkpoint_code " +
            "WHERE c.checkpoint_period_id = :checkpointPeriodId",
            nativeQuery = true)
    List<CheckpointPeriodConfigureProjection> findConfigureDetails(@Param("checkpointPeriodId") UUID checkpointPeriodId);
}
