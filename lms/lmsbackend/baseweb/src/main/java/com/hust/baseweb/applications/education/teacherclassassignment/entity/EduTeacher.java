package com.hust.baseweb.applications.education.teacherclassassignment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Teacher information.
 */
@Getter
@Setter
@Entity
@Table(name = "teacher")
public class EduTeacher {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "user_login_id")
    private String userLoginId;

    @Column(name = "max_hour_load")
    private double maxHourLoad;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

}
