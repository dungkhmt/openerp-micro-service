package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.AbsenceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface AbsenceRepo extends IBaseRepository<AbsenceEntity, UUID> {

    @Query(value = """
    SELECT * 
    FROM hr_absence 
    WHERE user_id IN :userIds 
    AND date BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    List<AbsenceEntity> findAbsencesWithDatesInRange(
        @Param("userIds") List<String> userIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}

