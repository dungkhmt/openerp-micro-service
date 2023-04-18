package com.hust.baseweb.applications.taskmanagement.entity;

import com.hust.baseweb.entity.StatusItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "backlog_task_assignable")
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_task_assignable_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "backlog_task_id")
    private Task task;

    @Column(name = "assigned_to_party_id")
    private UUID partyId;

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
    private Date creadtedStamp;
}
