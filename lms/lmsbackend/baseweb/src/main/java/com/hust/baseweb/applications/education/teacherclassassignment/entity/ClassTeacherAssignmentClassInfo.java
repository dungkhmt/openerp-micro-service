package com.hust.baseweb.applications.education.teacherclassassignment.entity;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.ClassId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Class information in an assignment plan.
 */
@Getter
@Setter
@Entity
@Table(name = "class_info")
@IdClass(ClassId.class)
public class ClassTeacherAssignmentClassInfo {

    @Id
    @Column(name = "id")
    private String classId;

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "semester_id")
    private String semesterId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "class_name")
    private String className;

    @Column(name = "credit")
    private String credit;

    @Column(name = "note")
    private String note;

    @Column(name = "class_program")
    private String program;

    @Column(name = "semester_type")
    private String semesterType;

    @Column(name = "enrollment")
    private int enrollment;

    @Column(name = "max_enrollment")
    private int maxEnrollment;

    @Column(name = "class_type")
    private String classType;

    @Column(name = "timetable")
    private String timetable;

    @Column(name = "lesson")
    private String lesson;

    @Column(name = "department_id")
    private String departmentId;

//    @Column(name = "teacher_id")
//    private String teacherId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "hour_load")
    private double hourLoad;
}
