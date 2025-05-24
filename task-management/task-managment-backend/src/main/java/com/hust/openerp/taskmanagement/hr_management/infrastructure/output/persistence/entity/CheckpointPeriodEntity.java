package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckpointPeriodStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_checkpoint_period")
public class CheckpointPeriodEntity extends AuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v1()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "checkpoint_date", length = 100)
    private String checkpointDate;

    @Column(name = "created_by_user_id", length = 60)
    private String createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    private CheckpointPeriodStatus status;

}