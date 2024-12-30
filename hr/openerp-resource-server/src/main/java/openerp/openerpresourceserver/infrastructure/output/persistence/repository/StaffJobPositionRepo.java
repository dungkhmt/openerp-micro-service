package openerp.openerpresourceserver.infrastructure.output.persistence.repository;


import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffJobPositionRepo extends IBaseRepository<StaffJobPositionEntity, StaffJobPositionId>  {
    @Query(value = "SELECT * FROM hr_staff_job_position " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<StaffJobPositionEntity> findLatestJobByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_job_position " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC ", nativeQuery = true)
    List<StaffJobPositionEntity> findHistoryJobByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_job_position sjp " +
            "WHERE sjp.user_id IN (:userIds) " +
            "AND sjp.from_date = (" +
            "   SELECT MAX(sjp2.from_date) " +
            "   FROM hr_staff_job_position sjp2 " +
            "   WHERE sjp2.user_id = sjp.user_id" +
            ")", nativeQuery = true)
    List<StaffJobPositionEntity> findLatestPositionsByUserIds(@Param("userIds") List<String> userIds);
}

