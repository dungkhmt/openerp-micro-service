package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;


import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.StaffJobPositionProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffJobPositionRepo extends IBaseRepository<StaffJobPositionEntity, StaffJobPositionId>  {
    @Query(value = "SELECT sjp.user_id AS userLoginId, jp.position_code AS jobPositionCode, " +
            "jp.position_name AS jobPositionName, jp.description AS description, " +
            "jp.status AS status, sjp.from_date AS fromDate, sjp.thru_date AS thruDate " +
            "FROM hr_staff_job_position sjp " +
            "JOIN hr_job_position jp ON sjp.position_code = jp.position_code " +
            "WHERE sjp.user_id = :userId " +
            "ORDER BY sjp.from_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<StaffJobPositionProjection> findLatestProjectionJobByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM hr_staff_job_position " +
            "WHERE user_id = :userId " +
            "ORDER BY from_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<StaffJobPositionEntity> findLatestJobByUserId(@Param("userId") String userId);

    @Query(value = "SELECT sjp.user_id AS userLoginId, jp.position_code AS jobPositionCode, " +
            "jp.position_name AS jobPositionName, jp.description AS description, " +
            "jp.status AS status, sjp.from_date AS fromDate, sjp.thru_date AS thruDate " +
            "FROM hr_staff_job_position sjp " +
            "JOIN hr_job_position jp ON sjp.position_code = jp.position_code " +
            "WHERE sjp.user_id = :userId " +
            "ORDER BY sjp.from_date DESC", nativeQuery = true)
    List<StaffJobPositionProjection> findHistoryJobByUserId(@Param("userId") String userId);

    @Query(value = "SELECT sjp.user_id AS userLoginId, jp.position_code AS jobPositionCode, " +
            "jp.position_name AS jobPositionName, jp.description AS description, " +
            "jp.status AS status, sjp.from_date AS fromDate, sjp.thru_date AS thruDate " +
            "FROM hr_staff_job_position sjp " +
            "JOIN hr_job_position jp ON sjp.position_code = jp.position_code " +
            "WHERE sjp.user_id IN (:userIds) " +
            "AND sjp.from_date = (" +
            "   SELECT MAX(sjp2.from_date) " +
            "   FROM hr_staff_job_position sjp2 " +
            "   WHERE sjp2.user_id = sjp.user_id" +
            ")", nativeQuery = true)
    List<StaffJobPositionProjection> findLatestPositionsByUserIds(@Param("userIds") List<String> userIds);
}

