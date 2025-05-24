package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointConfigureStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_configure")
public class CheckpointConfigureEntity extends AuditEntity{
    @Id
    @Column(name = "checkpoint_code", nullable = false, length = 100)
    private String checkpointCode;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private CheckpointConfigureStatus status;
}