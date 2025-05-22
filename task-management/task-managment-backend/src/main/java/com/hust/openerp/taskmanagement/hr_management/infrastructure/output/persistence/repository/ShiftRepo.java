package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.AbsenceEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.ShiftEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface ShiftRepo extends IBaseRepository<ShiftEntity, UUID> {

    @Query(value = """
    SELECT * 
    FROM hr_shift 
    WHERE (user_id IN :userIds or (:hasUnassigned and user_id IS NULL))
    AND date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<ShiftEntity> findShiftsWithDatesInRange(
        @Param("userIds") List<String> userIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("hasUnassigned") boolean hasUnassigned
    );
}

