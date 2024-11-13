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
@Table(name = "edu_course")
@EntityListeners(AuditingEntityListener.class)
public class EduCourse {

    @Id
    private String id;

    @Column(name = "course_name")
    private String name;

    @Column(name = "credit")
    private short credit;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    private Date createdStamp;
}
