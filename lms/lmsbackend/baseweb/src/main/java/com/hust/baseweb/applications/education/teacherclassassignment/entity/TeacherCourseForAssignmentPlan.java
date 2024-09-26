package com.hust.baseweb.applications.education.teacherclassassignment.entity;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherCoursePlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Limits courses assigned to a teacher in an assignment plan.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teacher_course_in_plan")
@IdClass(TeacherCoursePlanId.class)
public class TeacherCourseForAssignmentPlan {

    @Id
    @Column(name = "teacher_course_id")
    private UUID teacherCourseId;

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "priority")
    private int priority;

    // The following properties are redundant only for querying performance
    @Column(name = "teacher_id")
    private String teacherId;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "classType")
    private String classType;

//    @Column(name = "score")
//    private double score;
//
//    @Column(name = "last_updated_stamp")
//    private Date lastUpdatedStamp;
//
//    @Column(name = "created_stamp")
//    private Date createdStamp;

}
