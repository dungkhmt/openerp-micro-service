package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.AbsenceEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface AbsenceRepo extends IBaseRepository<AbsenceEntity, UUID>  {
    @Query(value = """
    SELECT *
    FROM hr_absence
    """, nativeQuery = true)
    List<AbsenceEntity> findAbsencesWithDatesInRange(List<String> userIds, LocalDate startDate, LocalDate endDate);
}

