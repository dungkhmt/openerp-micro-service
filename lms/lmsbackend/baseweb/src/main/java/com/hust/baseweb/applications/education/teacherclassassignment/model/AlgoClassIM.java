package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlgoClassIM {

    // TODO: xem xet loai bo cac truong khong can thiet

    private int id; // used for ???

    private String classId;

    private String classType;

    private String courseId; // ma mon hoc, vi du IT3011

    private String courseName;

    private String timetable; // example 1,41730,411145,7,8,9,11,12,13,B1-201;

    private double hourLoad; // gio quy doi cua lop (cd: 3 hours)

    private boolean pinned;

    public AlgoClassIM(
        String classId,
        String classType,
        String courseId,
        String courseName,
        String timetable,
        double hourLoad,
        boolean pinned
    ) {
        this.classId = classId;
        this.classType = classType;
        this.courseId = courseId;
        this.courseName = courseName;
        this.timetable = timetable;
        this.hourLoad = hourLoad;
        this.pinned = pinned;
    }
}
