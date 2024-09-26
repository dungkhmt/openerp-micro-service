package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_management_task_priority")
public class TaskPriority {

    @Id
    @Column(name = "id")
    private String priorityId;

    @Column(name = "name")
    private String priorityName;
}
