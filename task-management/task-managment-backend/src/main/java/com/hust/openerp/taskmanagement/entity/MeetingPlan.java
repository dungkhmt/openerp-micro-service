package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_meeting_plan")
public class MeetingPlan extends AbstractBaseEntity {

    @Id
    @Column
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status_id")
    private String statusId;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "registration_deadline")
    private Date registrationDeadline;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    private Status status;
}

