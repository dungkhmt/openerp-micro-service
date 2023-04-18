package com.hust.baseweb.applications.taskmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "backlog_task_priority")
public class TaskPriority {

    @Id
    @Column(name = "backlog_task_priority_id")
    private String priorityId;

    @Column(name = "backlog_task_priority_name")
    private String priorityName;
}
