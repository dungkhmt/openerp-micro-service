package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.HolidayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface HolidayRepo extends IBaseRepository<HolidayEntity, UUID>  {
    @Query("SELECT h FROM HolidayEntity h " +
        "WHERE (" +
        "   ( :startDate <= h.dates AND :endDate >= h.dates ) " +
        "   OR " +
        "   (h.type = 'EVERY_YEAR' AND " +
        "      (MONTH(:startDate) <= MONTH(h.dates) AND MONTH(:endDate) >= MONTH(h.dates)) AND " +
        "      (DAY(:startDate) <= DAY(h.dates) AND DAY(:endDate) >= DAY(h.dates))" +
        ") )")
    List<HolidayEntity> findHolidaysBetweenDates(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}

