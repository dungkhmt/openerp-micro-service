package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_checkinout")
public class CheckinoutEntity extends AuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v1()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", length = 60)
    private String userId;

    @Column(name = "time_point")
    private LocalDateTime timePoint;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "checkinout", length = 1)
    private CheckinoutType checkinout;

}