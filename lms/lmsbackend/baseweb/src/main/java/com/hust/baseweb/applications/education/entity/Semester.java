package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "edu_semester")
@EntityListeners(AuditingEntityListener.class)
public class Semester {

    @Id
    private short id;

    @Column(name = "semester_name")
    private String name;

    @Column(name = "is_active")
    private boolean active;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    private Date createdStamp;


}
