package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.HolidayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_holiday")
public class HolidayEntity extends AuditEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private HolidayType type;

    @Column(name = "event_dates", columnDefinition = "date[]")
    @ElementCollection
    private List<LocalDate> dates;
}