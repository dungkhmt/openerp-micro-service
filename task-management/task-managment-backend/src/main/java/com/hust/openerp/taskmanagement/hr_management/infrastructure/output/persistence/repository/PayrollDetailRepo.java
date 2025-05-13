package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.PayrollDetailEntity;

import java.util.List;
import java.util.UUID;

public interface PayrollDetailRepo extends IBaseRepository<PayrollDetailEntity, UUID> {
    List<PayrollDetailEntity> findByPayrollId(UUID payrollId);
}
