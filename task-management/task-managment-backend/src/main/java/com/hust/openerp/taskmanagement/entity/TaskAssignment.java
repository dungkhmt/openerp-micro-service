package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_management_task_assignment")
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "assigned_to_user_id")
    private String assigneeId;

    @OneToOne
    @JoinColumn(name = "status_id")
    private StatusItem statusItem;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "finished_date")
    private Date finishedDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;
}
