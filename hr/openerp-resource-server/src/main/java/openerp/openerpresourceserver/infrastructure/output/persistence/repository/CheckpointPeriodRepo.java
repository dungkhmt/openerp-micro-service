package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CheckpointPeriodRepo extends IBaseRepository<CheckpointPeriodEntity, UUID> {
    List<CheckpointPeriodEntity> findByIdIn(List<UUID> ids);
}
