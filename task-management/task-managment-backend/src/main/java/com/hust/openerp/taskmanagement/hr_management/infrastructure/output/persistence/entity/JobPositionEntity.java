package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;

@Getter
@Setter
@Entity
@Table(name = "hr_job_position")
public class JobPositionEntity extends AuditEntity{
    @Id
    @Column(name = "position_code", nullable = false, length = 100)
    private String positionCode;

    @Column(name = "position_name", length = 500)
    private String positionName;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "status", length = 45)
    @Enumerated(EnumType.STRING)
    private JobPositionStatus status;
}