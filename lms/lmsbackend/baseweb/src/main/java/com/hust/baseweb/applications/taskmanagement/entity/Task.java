package com.hust.baseweb.applications.taskmanagement.entity;

import com.hust.baseweb.entity.StatusItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "backlog_task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_task_id")
    private UUID id;

    @Column(name = "backlog_task_name")
    private String name;

    @Column(name = "backlog_description")
    private String description;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name = "attachment_paths")
    private String attachmentPaths;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @ManyToOne
    @JoinColumn(name = "backlog_project_id")
    private Project project;

    @OneToOne
    @JoinColumn(name = "status_id")
    private StatusItem statusItem;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private TaskPriority taskPriority;

    @ManyToOne
    @JoinColumn(name = "backlog_task_category_id")
    private TaskCategory taskCategory;

    public Task(Task task){
        this.setName(task.getName());
        this.setDescription(task.getDescription());
        this.setTaskCategory(task.getTaskCategory());
        this.setTaskPriority(task.getTaskPriority());
        this.setStatusItem(task.getStatusItem());
        this.setAttachmentPaths(task.getAttachmentPaths());
        this.setDueDate(task.getDueDate());
    }
}

