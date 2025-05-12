package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayrollDetailRepo extends JpaRepository<PayrollDetailEntity, UUID> {
    List<PayrollDetailEntity> findByPayrollId(UUID payrollId);
}
