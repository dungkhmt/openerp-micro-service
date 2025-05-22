package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_shift")
public class ShiftEntity extends AuditEntity{
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "slots")
    private Integer slots;

}