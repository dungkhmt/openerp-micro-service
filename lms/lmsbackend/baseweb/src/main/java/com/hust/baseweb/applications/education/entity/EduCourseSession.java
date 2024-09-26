package com.hust.baseweb.applications.education.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "edu_course_session")
public class EduCourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "course_id")
    private String courseId ;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "description")
    private String description;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated_stamp")
    private Date lastUpdated;
}
