package com.hust.openerp.taskmanagement.entity;

import java.util.Date;
import java.util.UUID;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_meeting_session")
public class MeetingSession extends AbstractBaseEntity {

    @Id
    @Column
    private UUID id;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private MeetingPlan meetingPlan;
}
