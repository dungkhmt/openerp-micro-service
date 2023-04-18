package com.hust.baseweb.applications.taskmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "backlog_task_skill")
public class TaskSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_task_skill_id")
    private UUID id;

    @Column(name = "backlog_task_id")
    private UUID taskId;

    @Column(name = "backlog_skill_id")
    private String skillId;
}
