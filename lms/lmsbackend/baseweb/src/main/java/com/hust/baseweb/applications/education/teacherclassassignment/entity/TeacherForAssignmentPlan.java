package com.hust.baseweb.applications.education.teacherclassassignment.entity;


import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherPlanId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Teacher participating in the assignment plan. Only used for module Teaching assignment.
 */
@Getter
@Setter
@Entity
@Table(name = "teacher_in_plan")
@IdClass(TeacherPlanId.class)
public class TeacherForAssignmentPlan {

    @Id
    @Column(name = "teacher_id")
    private String teacherId;

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    // In different assignment plans, this attribute is different
    @Column(name = "max_hour_load")
    private double maxHourLoad;

    @Column(name = "minimize_number_working_days")
    private String minimizeNumberWorkingDays;

//    @Column(name = "last_updated_stamp")
//    private Date lastUpdatedStamp;
//
//    @Column(name = "created_stamp")
//    private Date createdStamp;

}
