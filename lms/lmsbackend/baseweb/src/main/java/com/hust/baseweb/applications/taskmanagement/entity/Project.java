package com.hust.baseweb.applications.taskmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "backlog_project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_project_id")
    private UUID id;

    @Column(name = "backlog_project_code")
    private String code;

    @Column(name = "backlog_project_name")
    private String name;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

//    @OneToMany(mappedBy = "project")
//    private List<ProjectMember> projectMembers;

}
