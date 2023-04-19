package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeacherCoursePriorityForAssignmentPlanInputModel {
    private UUID planId;
    private String teacherId;
    private String courseId;
    private int priority;
}
