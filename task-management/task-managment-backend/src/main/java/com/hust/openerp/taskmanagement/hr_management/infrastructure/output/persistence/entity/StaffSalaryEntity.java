package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.SalaryType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "hr_staff_salary")
public class StaffSalaryEntity extends AuditEntity {
    @EmbeddedId
    private StaffSalaryId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;

    @Column(name = "salary")
    private Integer salary;

    @Column(name = "salary_type")
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
}