package com.hust.baseweb.applications.education.teacherclassassignment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Assigns a class to a teacher in an assignment plan.
 */
@Getter
@Setter
@Entity
@Table(name = "assignment_solution")
public class TeacherClassAssignmentSolution {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "pinned")
    private boolean pinned;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

}
