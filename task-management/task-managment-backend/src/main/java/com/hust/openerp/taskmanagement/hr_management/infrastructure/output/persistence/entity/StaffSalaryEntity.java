package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "salary", nullable = false)
    private Integer salary;

    @Column(name = "salary_type")
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
}