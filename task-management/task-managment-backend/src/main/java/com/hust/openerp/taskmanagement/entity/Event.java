package com.hust.openerp.taskmanagement.entity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "task_management_event")
public class Event {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;
    
    @Column(name = "due_date")
    private Date dueDate;
    
    @Column(name = "project_id")
    private UUID projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;
}
