package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OK
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConflictClassAssignedToTeacherModel {

    private String teacherId;

    private String teacherName;

    private String classCode1;

    private String timeTable1;

    private String courseName1;

    private String classCode2;

    private String timeTable2;

    private String courseName2;
}
