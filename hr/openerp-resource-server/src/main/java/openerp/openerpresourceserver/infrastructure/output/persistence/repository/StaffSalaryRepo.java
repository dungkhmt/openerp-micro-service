package openerp.openerpresourceserver.infrastructure.output.persistence.repository;


import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffSalaryEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffSalaryId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffSalaryRepo extends IBaseRepository<StaffSalaryEntity, StaffSalaryId>  {
    @Query(value = "SELECT * FROM hr_staff_salary " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<StaffSalaryEntity> findLatestSalaryByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_salary " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC ", nativeQuery = true)
    List<StaffSalaryEntity> findHistorySalaryByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_salary hss " +
            "WHERE hss.user_id IN (:userIds) " +
            "AND hss.from_date = (" +
            "   SELECT MAX(hss2.from_date) " +
            "   FROM hr_staff_salary hss2 " +
            "   WHERE hss2.user_id = hss.user_id" +
            ")", nativeQuery = true)
    List<StaffSalaryEntity> findLatestSalariesByUserIds(@Param("userIds") List<String> userIds);
}

