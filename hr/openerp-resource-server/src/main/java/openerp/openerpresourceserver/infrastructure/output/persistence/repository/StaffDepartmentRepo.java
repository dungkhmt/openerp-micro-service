package openerp.openerpresourceserver.infrastructure.output.persistence.repository;

import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffDepartmentRepo extends IBaseRepository<StaffDepartmentEntity, StaffDepartmentId> {

    @Query(value = "SELECT * FROM hr_staff_department " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<StaffDepartmentEntity> findLatestDepartmentByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_department " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC", nativeQuery = true)
    List<StaffDepartmentEntity> findHistoryDepartmentByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_department sd " +
            "WHERE sd.user_id IN (:userIds) " +
            "AND sd.from_date = (" +
            "   SELECT MAX(sd2.from_date) " +
            "   FROM hr_staff_department sd2 " +
            "   WHERE sd2.user_id = sd.user_id" +
            ")", nativeQuery = true)
    List<StaffDepartmentEntity> findLatestDepartmentsByUserIds(@Param("userIds") List<String> userIds);
}
