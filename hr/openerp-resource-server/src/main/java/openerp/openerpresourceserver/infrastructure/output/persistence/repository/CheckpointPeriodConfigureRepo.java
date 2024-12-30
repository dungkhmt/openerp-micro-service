package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodConfigureEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodConfigureId;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CheckpointPeriodConfigureRepo
        extends IBaseRepository<CheckpointPeriodConfigureEntity, CheckpointPeriodConfigureId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CheckpointPeriodConfigureEntity c WHERE c.id.checkpointPeriodId = :checkpointPeriodId")
    void deleteAllByCheckpointPeriodId(UUID checkpointPeriodId);
}
