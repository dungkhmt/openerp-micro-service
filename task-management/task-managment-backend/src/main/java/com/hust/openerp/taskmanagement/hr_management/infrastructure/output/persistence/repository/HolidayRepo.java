package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.HolidayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidayRepo extends IBaseRepository<HolidayEntity, UUID>  {
    @Query(value = """
    SELECT *
    FROM hr_holiday h
    WHERE EXISTS (
        SELECT 1
        FROM unnest(h.dates) AS d
        WHERE d BETWEEN CAST(:startDate AS date) AND CAST(:endDate AS date)
    )
    OR (
        h.type = 'EVERY_YEAR'
        AND EXISTS (
            SELECT 1
            FROM unnest(h.dates) AS d
            WHERE (
                (EXTRACT(MONTH FROM d), EXTRACT(DAY FROM d)) BETWEEN
                (EXTRACT(MONTH FROM CAST(:startDate AS date)), EXTRACT(DAY FROM CAST(:startDate AS date)))
                AND
                (EXTRACT(MONTH FROM CAST(:endDate AS date)), EXTRACT(DAY FROM CAST(:endDate AS date)))
            )
        )
    )
    """, nativeQuery = true)
    List<HolidayEntity> findHolidaysWithDatesInRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

}

