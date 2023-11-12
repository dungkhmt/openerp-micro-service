package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
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
@NoArgsConstructor
@Table(name = "task_management_project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false, insertable = false, updatable = false)
    private User createdByUser;
}
