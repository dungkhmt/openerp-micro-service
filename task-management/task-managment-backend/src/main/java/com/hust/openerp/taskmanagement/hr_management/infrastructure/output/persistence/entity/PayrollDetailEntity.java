package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.SalaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_payroll_details")
public class PayrollDetailEntity extends AuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "payroll_id", nullable = false)
    private UUID payrollId;

    @Size(max = 60)
    @Column(name = "user_id", length = 60)
    private String userId;

    @NotNull
    @Column(name = "salary", nullable = false)
    private Integer salary;

    @Column(name = "salary_type")
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @NotNull
    @Column(name = "is_paid_holiday", nullable = false)
    private Boolean isPaidHoliday = false;

    @NotNull
    @Column(name = "work_hours", nullable = false)
    private Float workHours;

    @NotNull
    @Column(name = "pair_leave_hours", nullable = false)
    private Float pairLeaveHours;

    @NotNull
    @Column(name = "unpair_leave_hours", nullable = false)
    private Float unpairLeaveHours;

    @NotNull
    @Column(name = "payroll_amount", nullable = false)
    private Integer payrollAmount;

}