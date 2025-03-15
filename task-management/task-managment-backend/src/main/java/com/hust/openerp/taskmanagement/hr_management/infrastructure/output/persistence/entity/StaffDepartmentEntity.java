package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "hr_staff_department")
public class StaffDepartmentEntity extends AuditEntity{
    @EmbeddedId
    private StaffDepartmentId id;

    @Column(name = "thru_date")
    private LocalDateTime thruDate;
}