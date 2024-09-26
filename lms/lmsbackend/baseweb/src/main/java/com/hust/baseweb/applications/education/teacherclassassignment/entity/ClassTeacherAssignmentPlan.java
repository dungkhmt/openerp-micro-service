package com.hust.baseweb.applications.education.teacherclassassignment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Meta data for assignment plan.
 */
@Getter
@Setter
@Entity
@Table(name = "plan")
public class ClassTeacherAssignmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

}
