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
@Deprecated
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;

    @Column(name = "task_id")
    private UUID taskId;

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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "assigned_to_user_id", referencedColumnName = "user_login_id", insertable = false, updatable = false)
    private User assignee;
}
