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
public class UpdateTeacherForAssignmentInputModel {

    private String teacherId;
    private UUID planId;
    private double hourLoad;
    private String minimizeNumberWorkingDays;
}
