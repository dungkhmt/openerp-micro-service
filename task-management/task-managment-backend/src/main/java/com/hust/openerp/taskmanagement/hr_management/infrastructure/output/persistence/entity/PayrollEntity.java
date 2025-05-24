package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.PayrollStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_payroll")
public class PayrollEntity extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "total_work_days", nullable = false)
    private Integer totalWorkDays;

    @NotNull
    @Column(name = "work_hours_per_day", nullable = false)
    private Float workHoursPerDay;

    @NotNull
    @Column(name = "total_holiday_days", nullable = false)
    private Integer totalHolidayDays;

    @NotNull
    @Column(name = "from_date", nullable = false)
    private LocalDate fromdate;

    @NotNull
    @Column(name = "thru_date", nullable = false)
    private LocalDate thruDate;

    @Size(max = 60)
    @Column(name = "created_by", nullable = false, length = 60)
    private String createdBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PayrollStatus status;

}