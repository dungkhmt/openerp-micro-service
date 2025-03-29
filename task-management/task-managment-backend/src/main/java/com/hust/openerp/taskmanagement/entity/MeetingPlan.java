package com.hust.openerp.taskmanagement.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "task_management_meeting_plan")
public class MeetingPlan {

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
    
    @OneToMany(mappedBy = "meetingPlan", fetch = FetchType.LAZY)
    private List<MeetingPlanUser> meetingPlanUsers;
}

