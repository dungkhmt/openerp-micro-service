package com.hust.baseweb.applications.taskmanagement.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "backlog_task_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_task_comment_id")
    private UUID commentId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name = "status")
    private boolean status;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;
}
