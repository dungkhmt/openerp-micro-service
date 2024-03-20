package com.hust.openerp.taskmanagement.entity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "task_management_task")
public class Task {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    @Column(name = "task_name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "progress")
    private Integer progress;

    @Column(name = "created_by_user_id")
    private String creatorId;

    @Column(name = "attachment_paths")
    private String attachmentPaths;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "priority_id")
    private String priorityId;

    @Column(name = "assignee_id")
    private String assigneeId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "ancestor_id")
    private UUID ancestorId;

    @Column(name = "lft")
    private Integer lft;

    @Column(name = "rgt")
    private Integer rgt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", insertable = false, updatable = false)
    private StatusItem status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id", insertable = false, updatable = false)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private TaskCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", insertable = false, updatable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", insertable = false, updatable = false)
    private User assignee;
}
