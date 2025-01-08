package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.CheckpointStaffProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CheckpointStaffRepo
        extends IBaseRepository<CheckpointStaffEntity, CheckpointStaffId> {
    @Query(value = "SELECT cs.user_id AS userId, cs.checkpoint_period_id AS checkpointPeriodId, " +
            "cs.checkpoint_code AS checkpointCode, cs.point AS point, cs.checked_by_user_id AS checkedByUserId, " +
            "cpc.coefficient AS coefficient, cpc.status AS periodStatus, " +
            "cc.name AS configureName, cc.description AS configureDescription, cc.status AS configureStatus " +
            "FROM hr_checkpoint_staff cs " +
            "JOIN hr_checkpoint_period_configure cpc ON cs.checkpoint_code = cpc.checkpoint_code " +
            "AND cs.checkpoint_period_id = cpc.checkpoint_period_id " +
            "JOIN hr_checkpoint_configure cc ON cs.checkpoint_code = cc.checkpoint_code " +
            "WHERE cs.checkpoint_period_id = :periodId AND cs.user_id = :userLoginId",
            nativeQuery = true)
    List<CheckpointStaffProjection> findCheckpointStaffDetails(@Param("periodId") UUID periodId,
                                                               @Param("userLoginId") String userLoginId);

    @Query(value = "SELECT cs.user_id AS userId, cs.checkpoint_period_id AS checkpointPeriodId, " +
            "cs.checkpoint_code AS checkpointCode, cs.point AS point, cs.checked_by_user_id AS checkedByUserId, " +
            "cpc.coefficient AS coefficient, cpc.status AS periodStatus, " +
            "cc.name AS configureName, cc.description AS configureDescription, cc.status AS configureStatus " +
            "FROM hr_checkpoint_staff cs " +
            "JOIN hr_checkpoint_period_configure cpc ON cs.checkpoint_code = cpc.checkpoint_code " +
            "AND cs.checkpoint_period_id = cpc.checkpoint_period_id " +
            "JOIN hr_checkpoint_configure cc ON cs.checkpoint_code = cc.checkpoint_code " +
            "WHERE cs.checkpoint_period_id = :periodId AND cs.user_id IN :userLoginIds",
            nativeQuery = true)
    List<CheckpointStaffProjection> findCheckpointStaffDetailsIn(@Param("periodId") UUID periodId,
                                                                 @Param("userLoginIds") List<String> userLoginIds);

}
