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
public class PairOfConflictTimetableClassModel {

    private String classId1;

    private String timetable1;

    private String timetableCode1;

    private String courseId1;

    private String courseName1;

    private String classId2;

    private String timetable2;

    private String timetableCode2;

    private String courseId2;

    private String courseName2;
}
