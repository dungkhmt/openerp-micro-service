package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;
import java.util.List;

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
    @NotEmpty
    private String code;

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(columnDefinition = "TEXT", name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by_user_id")
    private String createdUserId;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_login_id", nullable = false, insertable = false, updatable = false)
    private User creator;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectMember> members;
}
